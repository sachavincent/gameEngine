bl_info = {
    "name": "Custom File Exporter",
    "description": "",
    "author": "Alykas",
    "version": (1, 1, 1),
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
import shlex
from shutil import move
from random import randrange

from bpy.utils import (register_class, unregister_class)
from bpy.props import (StringProperty,
                        BoolProperty,
                        IntProperty,
                        FloatProperty,
                        FloatVectorProperty,
                        EnumProperty,
                        PointerProperty)
from bpy.types import (Panel,
                        AddonPreferences,
                        Operator,
                        PropertyGroup)
                      
MTL_FILENAME = "materials.mtl"
MODEL_FILENAME = "vertices.obj"
TEXTURE_FILENAME = "texture.png"
NORMALS_FILENAME = "normals.png"
COLLADA_FILENAME = "vertices.dae"

def exportDAE(folder, selected):
    daeFile = os.path.join(folder, COLLADA_FILENAME)
    
    bpy.ops.wm.collada_export(
        filepath=daeFile, 
        check_existing=True,
        active_uv_only=False, 
        use_object_instantiation=True, 
        use_blender_profile=True, 
        sort_by_name=False, 
        limit_precision=False, 
        keep_bind_info=False,
        apply_global_orientation=False, 
        sampling_rate=999999, 
        export_object_transformation_type=0, 
        export_object_transformation_type_selection='matrix', 
        export_animation_transformation_type=0, 
        export_animation_transformation_type_selection='matrix', 
        include_all_actions=True, 
        keep_keyframes=True, 
        keep_flat_curves=False, 
        keep_smooth_curves=False, 
        export_animation_type_selection='sample',
        include_animations=True, 
        open_sim=False, 
        deform_bones_only=False, 
        apply_modifiers=False,
        triangulate=True, 
        use_texture_copies=True, 
        export_global_up_selection='Z', 
        export_global_forward_selection='Y',
        include_children=True, 
        include_armatures=True, 
        include_shapekeys=True, 
        selected=True)
        
            
    with open(daeFile, "r") as f:
        daeFileData = f.readlines()
    
    inMaterialsLibrary = False
    inMaterial = False
    materialsIdEffect = {}
    mInfo = {}
    for i in range(len(daeFileData)):
        line = daeFileData[i]
        if "<library_materials>" in line:
            inMaterialsLibrary = True
        if "</library_materials>" in line:
            break
        if inMaterialsLibrary == False:
            continue
        # In Materials library:
        if "<material" in line:
            mInfo = {}
            materialInfo = line[14:-2]
            infos = shlex.split(materialInfo)
            for info in infos:
                infoParts = info.split("=")
                mInfo[infoParts[0]] = infoParts[1]
            inMaterial = True
        if "</material>" in line:
            inMaterial = False
        elif inMaterial == True and "<instance_effect" in line:
            materialsIdEffect[line[29:-4]] = mInfo["name"]
    
    inEffect = False
    inEffectsLibrary = False
    for i in range(len(daeFileData)):
        line = daeFileData[i]
        if "<library_effects>" in line:
            inEffectsLibrary = True
        if "</library_effects>" in line: # No more changes needed
            break
        if inEffectsLibrary == False:
            continue
        # In Effects library:
        if "<effect" in line:
            id = line[16:-3]
            materialName = materialsIdEffect.get(id)
            mat = bpy.data.materials.get(materialName)
            inEffect = True
        if "</effect>" in line:
            inEffect = False
        if "<lambert>" in line:
            if mat.use_nodes:
                nodeTree = mat.node_tree
                nodes = nodeTree.nodes
                BSDF = nodes.get("Principled BSDF")
                if BSDF is not None:
                    inputs = BSDF.inputs
                    specular = "{:.7f}".format(inputs["Specular"].default_value)
                    specularLine = "            <reflectivity>\n" + "              <float sid=\"reflectivity\">" + specular + "</float>\n            </reflectivity>\n"
                    daeFileData[i] = line + specularLine
        
        
        
    with open(daeFile, 'w') as f:
        f.writelines(daeFileData)
        
    return 0

def exportOBJ(folder, selected):
    mtlFile = os.path.join(folder, MTL_FILENAME)
    objFile = os.path.join(folder, MODEL_FILENAME)
    textureFile = os.path.join(folder, TEXTURE_FILENAME)
    normalsFile = os.path.join(folder, NORMALS_FILENAME)
    
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
        
    returnValue = 0
    textureSaved = False
    mixMaterials = {}
    for material in selected.data.materials:
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
            image.scale(512, 512) # Downscaling
            image.save_render(normalsFile)
            
    
    materialsFile = objFile[:-4] + '.mtl';
    with open(materialsFile, "r") as f:
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
            
    with open(materialsFile, 'w') as f:
        f.writelines(materialData)
    
    
    with open(objFile, "r") as f:
        objFileData = f.readlines()
        
    for i in range(len(objFileData)):
        if "mtllib" in objFileData[i]: # Rename file
            objFileData[i] = "mtllib " + MTL_FILENAME + "\n"
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
        nextline = objFileData[i + 1]
        if i < len(objFileData) - 1 and (nextline.startswith('g ') or nextline.startswith('usemtl ') or line.startswith('g (null)')):
            objFileData[i] = ''
        elif currentMaterial is not None:
            objFileData[i] = 'usemtl ' + currentMaterial + '::' + str(groupToInt.get(groupName)) + '\n'
            nbGroups += 1
        
    with open(objFile, 'w') as f:
        f.writelines(objFileData)
        
    move(materialsFile, mtlFile)
    
    return returnValue

def export(type, merge, folder):
    bpy.ops.wm.save_mainfile() 
    
    if not os.path.exists(folder):
        try:
            os.makedirs(folder)
        except FileNotFoundError:
            return 3
    
    returnValue = 4
    previousSelection = bpy.context.selected_objects
    previousActive = bpy.context.view_layer.objects.active
    selectedObj = bpy.context.selected_objects.copy()
    if len(selectedObj) == 0:
        return 1
    if len(selectedObj) > 1:
        if merge == False:
            return 2
        if bpy.context.view_layer.objects.active == None:
            return 10
        
    bpy.ops.object.duplicate() # Copies
    
    currActive = bpy.context.view_layer.objects.active
    for obj in bpy.context.selected_objects:
        bpy.context.view_layer.objects.active = obj
        for mod in obj.modifiers:
            if mod.type != 'ARMATURE':
                bpy.ops.object.modifier_apply(modifier = mod.name) # Apply modifiers
    bpy.context.view_layer.objects.active = currActive
    if merge == True:
        bpy.ops.object.join()

    selectedObj = bpy.context.selected_objects[0]
        
    if type == "DAE":
        returnValue = exportDAE(folder, selectedObj)
    elif type == "OBJ":
        returnValue = exportOBJ(folder, selectedObj)
    
    bpy.ops.object.delete() # Delete copies
    for obj in previousSelection: # Return to previous selection
        bpy.data.objects.get(obj.name).select_set(True)
    bpy.context.view_layer.objects.active = previousActive
    return returnValue

addon_name = "GameEngine File Exporter"


# ------------------------------------------------------------------------
#   settings in addon-preferences panel 
# ------------------------------------------------------------------------


# panel update function for PREFS_PT_MyPrefs panel 
def _update_panel_fnc(self, context):
    #
    # load addon custom-preferences 
    print(addon_name, ': update pref.panel function called')
    #
    main_panel = OBJECT_PT_my_panel
    #
    main_panel.bl_category = context.preferences.addons[addon_name].preferences.tab_label
    # re-register for update 
    unregister_class(main_panel)
    register_class(main_panel)


class PREFS_PT_MyPrefs(AddonPreferences):
    bl_idname = addon_name

    tab_label: StringProperty(
            name="Tab Label",
            description="Choose a label-name for the panel tab",
            default="New Addon",
            update=_update_panel_fnc)

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
    merge_bool : BoolProperty(
        name="Merge selection",
        description="If several objects are selected, they are merged",
        default = False)

    string_path : StringProperty(
        name="File path",
        description="Export path",
        default="",
        maxlen=1024,
        subtype='DIR_PATH')

    enum_fileFormat : EnumProperty(
        name="Export File Format",
        description="",
        items=[('OBJ', "OBJ+MTL Format", ""), ('DAE', "Collada Format", "")])

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
        res = export(exporterTool.enum_fileFormat, exporterTool.merge_bool, bpy.path.abspath(exporterTool.string_path))
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
            self.report({"ERROR"}, "No active object!")
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


#   def __init(self):
#       super( self, Panel ).__init__()
#       bl_category = bpy.context.preferences.addons[__name__].preferences.category 

    @classmethod
    def poll(self,context):
        return context.object is not None

    def draw(self, context):
        layout = self.layout
        scene = context.scene
        exporterTool = scene.my_tool

        layout.prop(exporterTool, "merge_bool")
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

    if km.keymap_items.get(OT_Exporter.bl_idname) is None:
        kmi = km.keymap_items.new(addon_name, 'P', 'PRESS', ctrl=True, alt=True)
    addon_keymaps.append(km)

    
def unregister():
    for cls in classes:
        unregister_class(cls)
    del bpy.types.Scene.my_tool
    
    wm = bpy.context.window_manager
    for km in addon_keymaps:
        wm.keyconfigs.addon.keymaps.remove(km)
    del addon_keymaps[:]