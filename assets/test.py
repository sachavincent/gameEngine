bl_info = {
    "name": "Custom File Exporter",
    "description": "",
    "author": "Alykas",
    "version": (1, 0, 6),
    "blender": (2, 80, 0),
    "location": "3D View > Tools",
    "warning": "", # used for warning icon and text in addons panel
    "wiki_url": "",
    "tracker_url": "",
    "category": "Development"
}

import bpy
import os
import re
from random import randrange

from bpy.utils import ( register_class, unregister_class )
from bpy.props import ( StringProperty,
                        BoolProperty,
                        IntProperty,
                        FloatProperty,
                        FloatVectorProperty,
                        EnumProperty,
                        PointerProperty,
                       )
from bpy.types import ( Panel,
                        AddonPreferences,
                        Operator,
                        PropertyGroup,
                      )


def resolveConflict(islandsDict, numIsland1, numIsland2):
    newIslandNum = min(numIsland1, numIsland2)
    keys = [key for key, value in islandsDict.items()
        if numIsland1 == value or numIsland2 == value]
    for key in keys:
        islandsDict[key] = newIslandNum
    return islandsDict

def export(type, folder):
    bpy.ops.wm.save_mainfile() 
    
    returnValue = 0

    selectedObj = bpy.context.selected_objects
    if len(selectedObj) == 0:
        return 1
    if len(selectedObj) > 1:
        return 2

    if not os.path.exists(folder):
        try:
            os.makedirs(folder)
        except FileNotFoundError:
            return 3     

    if type == "DAE":
        return 4
    
    mtlFile = os.path.join(folder, "vertices.mtl")
    objFile = os.path.join(folder, "vertices.obj")
    textureFile = os.path.join(folder, "texture.png")
    normalsFile = os.path.join(folder, "normals.png")
        
    selected = selectedObj.copy()
    for obj in selected:
        bpy.ops.export_scene.obj(
            filepath=objFile, 
            check_existing=True, 
            axis_forward='-Z', 
            axis_up='Y', 
            use_selection=True, 
            use_animation=False, 
            use_mesh_modifiers=True, 
            use_edges=True, 
            use_smooth_groups=False, 
            use_smooth_groups_bitflags=False, 
            use_normals=True, 
            use_uvs=True, 
            use_materials=True, 
            use_triangles=True, 
            use_nurbs=False,
            use_vertex_groups=True, 
            use_blen_objects=True, 
            group_by_object=False, 
            group_by_material=True, 
            keep_vertex_order=False, 
            global_scale=1, 
            path_mode='AUTO')
        
    textureSaved = False
    mixMaterials = {}
    for obj in selected:
        for materialslot in obj.material_slots:
            material = materialslot.material
            if material.use_nodes:
                nodeTree = material.node_tree
                nodes = nodeTree.nodes
                materialOut = nodes.get("Material Output")
                if materialOut is None:
                    continue
                materialInput = materialOut.inputs.get("Surface")
                if not materialInput.is_linked:
                    continue
                BSDF = materialInput.links[0].from_node
                colorInput = BSDF.inputs[0] # Color input
                if colorInput is None or not colorInput.is_linked:
                    continue
                colorInputNode = colorInput.links[0].from_node
                if colorInputNode.type == "MIX_RGB": # Mix
                    mixRGB = colorInputNode
                    if mixRGB is not None and len(mixRGB.inputs) > 0 and mixRGB.blend_type == "MIX":
                        mixRGBInput = mixRGB.inputs[0]
                        if mixRGBInput.is_linked:
                            link = mixRGBInput.links[0]
                            if link.from_node.type == "OBJECT_INFO" and link.from_socket.name == "Random":
                                factor = "SEED=" + str(randrange(1000000, 9999999))
                            else:
                                return 10
                        else:
                            factor = "F=" + str(mixRGBInput.default_value)
                            
                        value = mixRGB.name + ' ' + factor
                        colors = [mixRGB.inputs[1].default_value, mixRGB.inputs[2].default_value]
                        for e in colors:
                            value += ' ' + str(e[0])
                            value += ' ' + str(e[1])
                            value += ' ' + str(e[2])
                        mixMaterials[material.name] = value
                        break
                elif colorInputNode.type == "TEX_IMAGE": # Texture image
                    imageTexture = colorInputNode
                    if imageTexture is None:
                        continue
                    image = imageTexture.image
                    if image == None:
                        return 5
                    colorspace = image.colorspace_settings.name
                    if colorspace != "sRGB":
                        return 6
                    if not textureSaved:
                        if not os.path.exists(textureFile):
                            open(textureFile, "w").close()
                        image.save_render(textureFile)
                        textureSaved = True
                    
                normalNode = BSDF.inputs.get("Normal") # Normals
                if normalNode is None or not normalNode.is_linked:
                    continue
                normalInput = normalNode.links[0].from_node
                if normalInput.type != "NORMAL_MAP":
                    returnValue = 7
                    continue
                if normalInput.space != "TANGENT":
                    returnValue = 7
                    continue
                normalMapInput = normalInput.inputs.get("Color")
                if normalMapInput is None or not normalMapInput.is_linked:
                    returnValue = 7
                    continue
                normalColorInput = normalMapInput.links[0].from_node
                if normalColorInput.type != "TEX_IMAGE":
                    returnValue = 7
                    continue
                image = normalColorInput.image
                if image == None:
                    returnValue = 8
                    continue
                colorspace = image.colorspace_settings.name
                if colorspace != "Non-Color":
                    returnValue = 9
                    continue
                if not os.path.exists(normalsFile):
                    open(normalsFile, "w").close()
                image.scale(512, 512) # Downscaling
                image.save_render(normalsFile)
    
    mtlFileName = "materials.mtl"
    mtlFileNew = os.path.join(folder, mtlFileName)
    os.rename(mtlFile, mtlFileNew)
    mtlFile = mtlFileNew
    
    with open(mtlFile, "r") as f:
        materialData = f.readlines()
        
    currMaterial = None
    value = None
    for i in range(len(materialData)):
        line = materialData[i]
        if "newmtl" in line:
            currMaterial = None
            value = None
            
        for k, v in mixMaterials.items():
            if k in line:
                currMaterial = k
                value = v
                break
        if re.match('^(Kd)(.*?)$', line):
            if value is not None:
                materialData[i] = "Kd " + value + "\n"
            
    with open(mtlFile, 'w') as f:
        f.writelines(materialData)
    
    with open(objFile, "r") as f:
        objData = f.readlines()
    
    groupToInt = {}
    currentMaterial = None
    for i in range(len(objData)):
        line = objData[i]
        if line.startswith('mtllib'):
            objData[i] = 'mtllib ' + mtlFileName + '\n'
        if line.startswith('usemtl'):
            currentMaterial = line.split(" ")[1][:-1] # new Material
            nbGroups = 1
            continue
        if not line.startswith('g '):
            continue
        groupName = line[2:-1]
        found = False
        if groupToInt.get(groupName) is None:
            for obj in selected:
                if obj.vertex_groups.find(groupName) >= 0:
                    groupToInt[groupName] = nbGroups
                    nbGroups += 1
                    found = True
                    break
                print("Can't find " + groupName)
            if not found:
                objData[i] = ''
                continue
        nextline = objData[i + 1]
        if i < len(objData) - 1 and (nextline.startswith('g ') or nextline.startswith('usemtl ') or line.startswith('g (null)')):
            objData[i] = ''
        elif currentMaterial is not None:
            objData[i] = 'usemtl ' + currentMaterial + '::' + str(groupToInt.get(groupName)) + '\n'
            nbGroups += 1
        
    with open(objFile, 'w') as f:
        f.writelines(objData)
        
    return returnValue

addon_name = "GameEngine File Exporter"


# ------------------------------------------------------------------------
#   settings in addon-preferences panel 
# ------------------------------------------------------------------------


# panel update function for PREFS_PT_MyPrefs panel 
def _update_panel_fnc(self, context):
    #
    # load addon custom-preferences 
    print( addon_name, ': update pref.panel function called' )
    #
    main_panel =  OBJECT_PT_my_panel
    #
    main_panel .bl_category = context .preferences.addons[addon_name] .preferences.tab_label
    # re-register for update 
    unregister_class( main_panel )
    register_class( main_panel )


class PREFS_PT_MyPrefs(AddonPreferences):
    bl_idname = addon_name

    tab_label: StringProperty(
            name="Tab Label",
            description="Choose a label-name for the panel tab",
            default="New Addon",
            update=_update_panel_fnc
    )

    def draw(self, context):
        layout = self.layout

        row = layout.row()
        col = row.column()
        col.label(text="Tab Label:")
        col.prop(self, "tab_label", text="")


# ------------------------------------------------------------------------
#   properties visible in the addon-panel 
# ------------------------------------------------------------------------

class PG_MyProperties (PropertyGroup):

    my_bool : BoolProperty(
        name="TODO",
        description="",
        default = False
        )

    string_path : StringProperty(
        name="File path",
        description="Export path",
        default="",
        maxlen=1024,
        subtype='DIR_PATH',
        )

    enum_fileFormat : EnumProperty(
        name="Export File Format",
        description="",
        items=[ ('OBJ', "OBJ+MTL Format", ""),
                ('DAE', "Collada Format", ""),
               ]
        )

# ------------------------------------------------------------------------
#   operators
# ------------------------------------------------------------------------

class OT_Exporter(bpy.types.Operator):
    bl_label = "Export"
    bl_idname = "object.exporter"

    def execute(self, context):
        scene = context.scene
        exporterTool = scene.my_tool

        options = ['FINISHED', 'CANCELLED']
        res = export(exporterTool.enum_fileFormat, bpy.path.abspath(exporterTool.string_path))
        if res == 0:
            self.report({"INFO"}, "Exported '" + bpy.context.selected_objects[0].name + "' successfully!")  
        elif res == 1:
            self.report({"ERROR"}, "No object selected")
        elif res == 2:
            self.report({"ERROR"}, "Please select only 1 Object")
        elif res == 3:
            self.report({"ERROR_INVALID_INPUT"}, "Incorrect Path")
        elif res == 4:
            self.report({"ERROR_INVALID_INPUT"}, "Collada File Format not supported!")
        elif res == 5:
            self.report({"ERROR"}, "Missing material texture!")
        elif res == 6:
            self.report({"ERROR"}, "Wrong Texture Type!")
        elif res == 7:
            self.report({"WARNING"}, "Unable to export Normals due to wrong parameters")
        elif res == 8:
            self.report({"WARNING"}, "Missing Normals file!")
        elif res == 9:
            self.report({"WARNING"}, "Wrong Normals Color space!")
        elif res == 10:
            self.report({"ERROR"}, "MIX Input not supported!")
        return {options[min(1, res)]}

# ------------------------------------------------------------------------
#   addon - panel -- visible in objectmode
# ------------------------------------------------------------------------

class OBJECT_PT_my_panel (Panel):
    bl_idname = "OBJECT_PT_my_panel"
    bl_label = "File Exporter"
    bl_space_type = "VIEW_3D"   
    bl_region_type = "UI"
    bl_category = "Tool"  # note: replaced by preferences-setting in register function 
    bl_context = "objectmode"   

    @classmethod
    def poll(self,context):
        return context.object is not None

    def draw(self, context):
        layout = self.layout
        scene = context.scene
        exporterTool = scene.my_tool

        layout.prop(exporterTool, "my_bool")
        layout.prop(exporterTool, "enum_fileFormat", text="") 
        layout.prop(exporterTool, "string_path")
        layout.operator("object.exporter")


# ------------------------------------------------------------------------
# register and unregister
# ------------------------------------------------------------------------

classes = (
    PG_MyProperties,
    OT_Exporter,
    OBJECT_PT_my_panel, 
    PREFS_PT_MyPrefs, 
)

addon_keymaps = []

def register():
    for cls in classes:
        register_class(cls)
    bpy.types.Scene.my_tool = PointerProperty(type=PG_MyProperties)
    
    wm = bpy.context.window_manager
    km = wm.keyconfigs.addon.keymaps.new(name='Object Mode', space_type='EMPTY')

    if km.keymap_items.get("object.exporter") is None:
        kmi = km.keymap_items.new("object.exporter", 'P', 'PRESS', ctrl=True, alt=True)
    addon_keymaps.append(km)

    
def unregister():
    wm = bpy.context.window_manager
    for km in addon_keymaps:
        wm.keyconfigs.addon.keymaps.remove(km)
    del addon_keymaps[:]
    
    for cls in classes:
        unregister_class(cls)
    del bpy.types.Scene.my_tool