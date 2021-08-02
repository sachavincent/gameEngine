package util.math;

import java.io.Serializable;
import java.nio.FloatBuffer;

public class Matrix4f extends Matrix implements Serializable {

    private static final long  serialVersionUID = 1L;
    public               float m00;
    public               float m01;
    public               float m02;
    public               float m03;
    public               float m10;
    public               float m11;
    public               float m12;
    public               float m13;
    public               float m20;
    public               float m21;
    public               float m22;
    public               float m23;
    public               float m30;
    public               float m31;
    public               float m32;
    public               float m33;

    public Matrix4f() {
        this.setIdentity();
    }

    public Matrix4f(Matrix4f src) {
        this.load(src);
    }

    public String toString() {
        StringBuilder buf = new StringBuilder();
        buf.append(String.format("%.5f", this.m00)).append(' ').append(String.format("%.5f", this.m20)).append(' ')
                .append(String.format("%.5f", this.m20)).append(' ').append(String.format("%.5f", this.m30))
                .append('\n');
        buf.append(String.format("%.5f", this.m01)).append(' ').append(String.format("%.5f", this.m11)).append(' ')
                .append(String.format("%.5f", this.m21)).append(' ').append(String.format("%.5f", this.m31))
                .append('\n');
        buf.append(String.format("%.5f", this.m02)).append(' ').append(String.format("%.5f", this.m12)).append(' ')
                .append(String.format("%.5f", this.m22)).append(' ').append(String.format("%.5f", this.m32))
                .append('\n');
        buf.append(String.format("%.5f", this.m03)).append(' ').append(String.format("%.5f", this.m13)).append(' ')
                .append(String.format("%.5f", this.m23)).append(' ').append(String.format("%.5f", this.m33))
                .append('\n');
        return buf.toString();
    }

    public Matrix setIdentity() {
        return setIdentity(this);
    }

    public static Matrix4f setIdentity(Matrix4f m) {
        m.m00 = 1.0F;
        m.m01 = 0.0F;
        m.m02 = 0.0F;
        m.m03 = 0.0F;
        m.m10 = 0.0F;
        m.m11 = 1.0F;
        m.m12 = 0.0F;
        m.m13 = 0.0F;
        m.m20 = 0.0F;
        m.m21 = 0.0F;
        m.m22 = 1.0F;
        m.m23 = 0.0F;
        m.m30 = 0.0F;
        m.m31 = 0.0F;
        m.m32 = 0.0F;
        m.m33 = 1.0F;
        return m;
    }

    public Matrix setZero() {
        return setZero(this);
    }

    public static Matrix4f setZero(Matrix4f m) {
        m.m00 = 0.0F;
        m.m01 = 0.0F;
        m.m02 = 0.0F;
        m.m03 = 0.0F;
        m.m10 = 0.0F;
        m.m11 = 0.0F;
        m.m12 = 0.0F;
        m.m13 = 0.0F;
        m.m20 = 0.0F;
        m.m21 = 0.0F;
        m.m22 = 0.0F;
        m.m23 = 0.0F;
        m.m30 = 0.0F;
        m.m31 = 0.0F;
        m.m32 = 0.0F;
        m.m33 = 0.0F;
        return m;
    }

    public Matrix4f load(Matrix4f src) {
        return load(src, this);
    }

    public static Matrix4f load(Matrix4f src, Matrix4f dest) {
        if (dest == null) {
            dest = new Matrix4f();
        }

        dest.m00 = src.m00;
        dest.m01 = src.m01;
        dest.m02 = src.m02;
        dest.m03 = src.m03;
        dest.m10 = src.m10;
        dest.m11 = src.m11;
        dest.m12 = src.m12;
        dest.m13 = src.m13;
        dest.m20 = src.m20;
        dest.m21 = src.m21;
        dest.m22 = src.m22;
        dest.m23 = src.m23;
        dest.m30 = src.m30;
        dest.m31 = src.m31;
        dest.m32 = src.m32;
        dest.m33 = src.m33;
        return dest;
    }

    public Matrix load(FloatBuffer buf) {
        this.m00 = buf.get();
        this.m01 = buf.get();
        this.m02 = buf.get();
        this.m03 = buf.get();
        this.m10 = buf.get();
        this.m11 = buf.get();
        this.m12 = buf.get();
        this.m13 = buf.get();
        this.m20 = buf.get();
        this.m21 = buf.get();
        this.m22 = buf.get();
        this.m23 = buf.get();
        this.m30 = buf.get();
        this.m31 = buf.get();
        this.m32 = buf.get();
        this.m33 = buf.get();
        return this;
    }

    public Matrix loadTranspose(FloatBuffer buf) {
        this.m00 = buf.get();
        this.m10 = buf.get();
        this.m20 = buf.get();
        this.m30 = buf.get();
        this.m01 = buf.get();
        this.m11 = buf.get();
        this.m21 = buf.get();
        this.m31 = buf.get();
        this.m02 = buf.get();
        this.m12 = buf.get();
        this.m22 = buf.get();
        this.m32 = buf.get();
        this.m03 = buf.get();
        this.m13 = buf.get();
        this.m23 = buf.get();
        this.m33 = buf.get();
        return this;
    }

    public FloatBuffer store(FloatBuffer buf) {
        buf.put(this.m00);
        buf.put(this.m01);
        buf.put(this.m02);
        buf.put(this.m03);
        buf.put(this.m10);
        buf.put(this.m11);
        buf.put(this.m12);
        buf.put(this.m13);
        buf.put(this.m20);
        buf.put(this.m21);
        buf.put(this.m22);
        buf.put(this.m23);
        buf.put(this.m30);
        buf.put(this.m31);
        buf.put(this.m32);
        buf.put(this.m33);
        return buf;
    }


    public FloatBuffer store(int index, FloatBuffer buf) throws IndexOutOfBoundsException {
        buf.put(index, this.m00);
        buf.put(index + 1, this.m01);
        buf.put(index + 2, this.m02);
        buf.put(index + 3, this.m03);
        buf.put(index + 4, this.m10);
        buf.put(index + 5, this.m11);
        buf.put(index + 6, this.m12);
        buf.put(index + 7, this.m13);
        buf.put(index + 8, this.m20);
        buf.put(index + 9, this.m21);
        buf.put(index + 10, this.m22);
        buf.put(index + 11, this.m23);
        buf.put(index + 12, this.m30);
        buf.put(index + 13, this.m31);
        buf.put(index + 14, this.m32);
        buf.put(index + 15, this.m33);
        return buf;
    }

    public Matrix storeTranspose(FloatBuffer buf) {
        buf.put(this.m00);
        buf.put(this.m10);
        buf.put(this.m20);
        buf.put(this.m30);
        buf.put(this.m01);
        buf.put(this.m11);
        buf.put(this.m21);
        buf.put(this.m31);
        buf.put(this.m02);
        buf.put(this.m12);
        buf.put(this.m22);
        buf.put(this.m32);
        buf.put(this.m03);
        buf.put(this.m13);
        buf.put(this.m23);
        buf.put(this.m33);
        return this;
    }

    public Matrix store3f(FloatBuffer buf) {
        buf.put(this.m00);
        buf.put(this.m01);
        buf.put(this.m02);
        buf.put(this.m10);
        buf.put(this.m11);
        buf.put(this.m12);
        buf.put(this.m20);
        buf.put(this.m21);
        buf.put(this.m22);
        return this;
    }

    public static Matrix4f add(Matrix4f left, Matrix4f right, Matrix4f dest) {
        if (dest == null) {
            dest = new Matrix4f();
        }

        dest.m00 = left.m00 + right.m00;
        dest.m01 = left.m01 + right.m01;
        dest.m02 = left.m02 + right.m02;
        dest.m03 = left.m03 + right.m03;
        dest.m10 = left.m10 + right.m10;
        dest.m11 = left.m11 + right.m11;
        dest.m12 = left.m12 + right.m12;
        dest.m13 = left.m13 + right.m13;
        dest.m20 = left.m20 + right.m20;
        dest.m21 = left.m21 + right.m21;
        dest.m22 = left.m22 + right.m22;
        dest.m23 = left.m23 + right.m23;
        dest.m30 = left.m30 + right.m30;
        dest.m31 = left.m31 + right.m31;
        dest.m32 = left.m32 + right.m32;
        dest.m33 = left.m33 + right.m33;
        return dest;
    }

    public static Matrix4f sub(Matrix4f left, Matrix4f right, Matrix4f dest) {
        if (dest == null) {
            dest = new Matrix4f();
        }

        dest.m00 = left.m00 - right.m00;
        dest.m01 = left.m01 - right.m01;
        dest.m02 = left.m02 - right.m02;
        dest.m03 = left.m03 - right.m03;
        dest.m10 = left.m10 - right.m10;
        dest.m11 = left.m11 - right.m11;
        dest.m12 = left.m12 - right.m12;
        dest.m13 = left.m13 - right.m13;
        dest.m20 = left.m20 - right.m20;
        dest.m21 = left.m21 - right.m21;
        dest.m22 = left.m22 - right.m22;
        dest.m23 = left.m23 - right.m23;
        dest.m30 = left.m30 - right.m30;
        dest.m31 = left.m31 - right.m31;
        dest.m32 = left.m32 - right.m32;
        dest.m33 = left.m33 - right.m33;
        return dest;
    }

    public Matrix4f mulAffine(Matrix4f right, Matrix4f dest) {
        float m00 = this.m00, m01 = this.m01, m02 = this.m02;
        float m10 = this.m10, m11 = this.m11, m12 = this.m12;
        float m20 = this.m20, m21 = this.m21, m22 = this.m22;
        float rm00 = right.m00, rm01 = right.m01, rm02 = right.m02;
        float rm10 = right.m10, rm11 = right.m11, rm12 = right.m12;
        float rm20 = right.m20, rm21 = right.m21, rm22 = right.m22;
        float rm30 = right.m30, rm31 = right.m31, rm32 = right.m32;
        return dest
                ._m00(Maths.fma(m00, rm00, Maths.fma(m10, rm01, m20 * rm02)))
                ._m01(Maths.fma(m01, rm00, Maths.fma(m11, rm01, m21 * rm02)))
                ._m02(Maths.fma(m02, rm00, Maths.fma(m12, rm01, m22 * rm02)))
                ._m03(m03)
                ._m10(Maths.fma(m00, rm10, Maths.fma(m10, rm11, m20 * rm12)))
                ._m11(Maths.fma(m01, rm10, Maths.fma(m11, rm11, m21 * rm12)))
                ._m12(Maths.fma(m02, rm10, Maths.fma(m12, rm11, m22 * rm12)))
                ._m13(m13)
                ._m20(Maths.fma(m00, rm20, Maths.fma(m10, rm21, m20 * rm22)))
                ._m21(Maths.fma(m01, rm20, Maths.fma(m11, rm21, m21 * rm22)))
                ._m22(Maths.fma(m02, rm20, Maths.fma(m12, rm21, m22 * rm22)))
                ._m23(m23)
                ._m30(Maths.fma(m00, rm30, Maths.fma(m10, rm31, Maths.fma(m20, rm32, m30))))
                ._m31(Maths.fma(m01, rm30, Maths.fma(m11, rm31, Maths.fma(m21, rm32, m31))))
                ._m32(Maths.fma(m02, rm30, Maths.fma(m12, rm31, Maths.fma(m22, rm32, m32))))
                ._m33(m33);
    }

    public static Matrix4f mul(Matrix4f left, Matrix4f right, Matrix4f dest) {
        if (dest == null) {
            dest = new Matrix4f();
        }

        float m00 = left.m00 * right.m00 + left.m10 * right.m01 + left.m20 * right.m02 + left.m30 * right.m03;
        float m01 = left.m01 * right.m00 + left.m11 * right.m01 + left.m21 * right.m02 + left.m31 * right.m03;
        float m02 = left.m02 * right.m00 + left.m12 * right.m01 + left.m22 * right.m02 + left.m32 * right.m03;
        float m03 = left.m03 * right.m00 + left.m13 * right.m01 + left.m23 * right.m02 + left.m33 * right.m03;
        float m10 = left.m00 * right.m10 + left.m10 * right.m11 + left.m20 * right.m12 + left.m30 * right.m13;
        float m11 = left.m01 * right.m10 + left.m11 * right.m11 + left.m21 * right.m12 + left.m31 * right.m13;
        float m12 = left.m02 * right.m10 + left.m12 * right.m11 + left.m22 * right.m12 + left.m32 * right.m13;
        float m13 = left.m03 * right.m10 + left.m13 * right.m11 + left.m23 * right.m12 + left.m33 * right.m13;
        float m20 = left.m00 * right.m20 + left.m10 * right.m21 + left.m20 * right.m22 + left.m30 * right.m23;
        float m21 = left.m01 * right.m20 + left.m11 * right.m21 + left.m21 * right.m22 + left.m31 * right.m23;
        float m22 = left.m02 * right.m20 + left.m12 * right.m21 + left.m22 * right.m22 + left.m32 * right.m23;
        float m23 = left.m03 * right.m20 + left.m13 * right.m21 + left.m23 * right.m22 + left.m33 * right.m23;
        float m30 = left.m00 * right.m30 + left.m10 * right.m31 + left.m20 * right.m32 + left.m30 * right.m33;
        float m31 = left.m01 * right.m30 + left.m11 * right.m31 + left.m21 * right.m32 + left.m31 * right.m33;
        float m32 = left.m02 * right.m30 + left.m12 * right.m31 + left.m22 * right.m32 + left.m32 * right.m33;
        float m33 = left.m03 * right.m30 + left.m13 * right.m31 + left.m23 * right.m32 + left.m33 * right.m33;
        dest.m00 = m00;
        dest.m01 = m01;
        dest.m02 = m02;
        dest.m03 = m03;
        dest.m10 = m10;
        dest.m11 = m11;
        dest.m12 = m12;
        dest.m13 = m13;
        dest.m20 = m20;
        dest.m21 = m21;
        dest.m22 = m22;
        dest.m23 = m23;
        dest.m30 = m30;
        dest.m31 = m31;
        dest.m32 = m32;
        dest.m33 = m33;
        return dest;
    }

    public static Vector4f transform(Matrix4f left, Vector4f right, Vector4f dest) {
        if (dest == null) {
            dest = new Vector4f();
        }

        float x = left.m00 * right.x + left.m10 * right.y + left.m20 * right.z + left.m30 * right.w;
        float y = left.m01 * right.x + left.m11 * right.y + left.m21 * right.z + left.m31 * right.w;
        float z = left.m02 * right.x + left.m12 * right.y + left.m22 * right.z + left.m32 * right.w;
        float w = left.m03 * right.x + left.m13 * right.y + left.m23 * right.z + left.m33 * right.w;
        dest.x = x;
        dest.y = y;
        dest.z = z;
        dest.w = w;
        return dest;
    }

    public Matrix transpose() {
        return this.transpose(this);
    }

    public Matrix4f translate(Vector2f vec) {
        return this.translate(vec, this);
    }

    public Matrix4f translate(Vector3f vec) {
        return this.translate(vec, this);
    }

    public Matrix4f scale(Vector3f vec) {
        return scale(vec, this, this);
    }

    public static Matrix4f scale(Vector3f vec, Matrix4f src, Matrix4f dest) {
        if (dest == null) {
            dest = new Matrix4f();
        }

        dest.m00 = src.m00 * vec.x;
        dest.m01 = src.m01 * vec.x;
        dest.m02 = src.m02 * vec.x;
        dest.m03 = src.m03 * vec.x;
        dest.m10 = src.m10 * vec.y;
        dest.m11 = src.m11 * vec.y;
        dest.m12 = src.m12 * vec.y;
        dest.m13 = src.m13 * vec.y;
        dest.m20 = src.m20 * vec.z;
        dest.m21 = src.m21 * vec.z;
        dest.m22 = src.m22 * vec.z;
        dest.m23 = src.m23 * vec.z;
        return dest;
    }

    public Matrix4f rotate(float angle, Vector3f axis) {
        return this.rotate(angle, axis, this);
    }

    public Matrix4f rotate(float angle, Vector3f axis, Matrix4f dest) {
        return rotate(angle, axis, this, dest);
    }

    public static Matrix4f rotate(float angle, Vector3f axis, Matrix4f src, Matrix4f dest) {
        if (dest == null) {
            dest = new Matrix4f();
        }

        float c = (float) Math.cos(angle);
        float s = (float) Math.sin(angle);
        float oneminusc = 1.0F - c;
        float xy = axis.x * axis.y;
        float yz = axis.y * axis.z;
        float xz = axis.x * axis.z;
        float xs = axis.x * s;
        float ys = axis.y * s;
        float zs = axis.z * s;
        float f00 = axis.x * axis.x * oneminusc + c;
        float f01 = xy * oneminusc + zs;
        float f02 = xz * oneminusc - ys;
        float f10 = xy * oneminusc - zs;
        float f11 = axis.y * axis.y * oneminusc + c;
        float f12 = yz * oneminusc + xs;
        float f20 = xz * oneminusc + ys;
        float f21 = yz * oneminusc - xs;
        float f22 = axis.z * axis.z * oneminusc + c;
        float t00 = src.m00 * f00 + src.m10 * f01 + src.m20 * f02;
        float t01 = src.m01 * f00 + src.m11 * f01 + src.m21 * f02;
        float t02 = src.m02 * f00 + src.m12 * f01 + src.m22 * f02;
        float t03 = src.m03 * f00 + src.m13 * f01 + src.m23 * f02;
        float t10 = src.m00 * f10 + src.m10 * f11 + src.m20 * f12;
        float t11 = src.m01 * f10 + src.m11 * f11 + src.m21 * f12;
        float t12 = src.m02 * f10 + src.m12 * f11 + src.m22 * f12;
        float t13 = src.m03 * f10 + src.m13 * f11 + src.m23 * f12;
        dest.m20 = src.m00 * f20 + src.m10 * f21 + src.m20 * f22;
        dest.m21 = src.m01 * f20 + src.m11 * f21 + src.m21 * f22;
        dest.m22 = src.m02 * f20 + src.m12 * f21 + src.m22 * f22;
        dest.m23 = src.m03 * f20 + src.m13 * f21 + src.m23 * f22;
        dest.m00 = t00;
        dest.m01 = t01;
        dest.m02 = t02;
        dest.m03 = t03;
        dest.m10 = t10;
        dest.m11 = t11;
        dest.m12 = t12;
        dest.m13 = t13;
        return dest;
    }

    public Matrix4f translate(Vector3f vec, Matrix4f dest) {
        return translate(vec, this, dest);
    }

    public static Matrix4f translate(Vector3f vec, Matrix4f src, Matrix4f dest) {
        if (vec == null || src == null)
            return null;

        if (dest == null) {
            dest = new Matrix4f();
        }

        dest.m30 += src.m00 * vec.x + src.m10 * vec.y + src.m20 * vec.z;
        dest.m31 += src.m01 * vec.x + src.m11 * vec.y + src.m21 * vec.z;
        dest.m32 += src.m02 * vec.x + src.m12 * vec.y + src.m22 * vec.z;
        dest.m33 += src.m03 * vec.x + src.m13 * vec.y + src.m23 * vec.z;
        return dest;
    }

    public Matrix4f translate(Vector2f vec, Matrix4f dest) {
        return translate(vec, this, dest);
    }

    public static Matrix4f translate(Vector2f vec, Matrix4f src, Matrix4f dest) {
        if (dest == null) {
            dest = new Matrix4f();
        }

        dest.m30 += src.m00 * vec.x + src.m10 * vec.y;
        dest.m31 += src.m01 * vec.x + src.m11 * vec.y;
        dest.m32 += src.m02 * vec.x + src.m12 * vec.y;
        dest.m33 += src.m03 * vec.x + src.m13 * vec.y;
        return dest;
    }

    public Matrix4f transpose(Matrix4f dest) {
        return transpose(this, dest);
    }

    public static Matrix4f transpose(Matrix4f src, Matrix4f dest) {
        if (dest == null) {
            dest = new Matrix4f();
        }

        float m00 = src.m00;
        float m01 = src.m10;
        float m02 = src.m20;
        float m03 = src.m30;
        float m10 = src.m01;
        float m11 = src.m11;
        float m12 = src.m21;
        float m13 = src.m31;
        float m20 = src.m02;
        float m21 = src.m12;
        float m22 = src.m22;
        float m23 = src.m32;
        float m30 = src.m03;
        float m31 = src.m13;
        float m32 = src.m23;
        float m33 = src.m33;
        dest.m00 = m00;
        dest.m01 = m01;
        dest.m02 = m02;
        dest.m03 = m03;
        dest.m10 = m10;
        dest.m11 = m11;
        dest.m12 = m12;
        dest.m13 = m13;
        dest.m20 = m20;
        dest.m21 = m21;
        dest.m22 = m22;
        dest.m23 = m23;
        dest.m30 = m30;
        dest.m31 = m31;
        dest.m32 = m32;
        dest.m33 = m33;
        return dest;
    }

    public float determinant() {
        float f = this.m00 *
                (this.m11 * this.m22 * this.m33 + this.m12 * this.m23 * this.m31 + this.m13 * this.m21 * this.m32 -
                        this.m13 * this.m22 * this.m31 - this.m11 * this.m23 * this.m32 -
                        this.m12 * this.m21 * this.m33);
        f -= this.m01 *
                (this.m10 * this.m22 * this.m33 + this.m12 * this.m23 * this.m30 + this.m13 * this.m20 * this.m32 -
                        this.m13 * this.m22 * this.m30 - this.m10 * this.m23 * this.m32 -
                        this.m12 * this.m20 * this.m33);
        f += this.m02 *
                (this.m10 * this.m21 * this.m33 + this.m11 * this.m23 * this.m30 + this.m13 * this.m20 * this.m31 -
                        this.m13 * this.m21 * this.m30 - this.m10 * this.m23 * this.m31 -
                        this.m11 * this.m20 * this.m33);
        f -= this.m03 *
                (this.m10 * this.m21 * this.m32 + this.m11 * this.m22 * this.m30 + this.m12 * this.m20 * this.m31 -
                        this.m12 * this.m21 * this.m30 - this.m10 * this.m22 * this.m31 -
                        this.m11 * this.m20 * this.m32);
        return f;
    }

    private static float determinant3x3(float t00, float t01, float t02, float t10, float t11, float t12, float t20,
            float t21, float t22) {
        return t00 * (t11 * t22 - t12 * t21) + t01 * (t12 * t20 - t10 * t22) + t02 * (t10 * t21 - t11 * t20);
    }

    public Matrix invert() {
        return invert(this, this);
    }

    public static Matrix4f invert(Matrix4f src, Matrix4f dest) {
        float determinant = src.determinant();
        if (determinant != 0.0F) {
            if (dest == null) {
                dest = new Matrix4f();
            }

            float determinant_inv = 1.0F / determinant;
            float t00 = determinant3x3(src.m11, src.m12, src.m13, src.m21, src.m22, src.m23, src.m31, src.m32, src.m33);
            float t01 = -determinant3x3(src.m10, src.m12, src.m13, src.m20, src.m22, src.m23, src.m30, src.m32,
                    src.m33);
            float t02 = determinant3x3(src.m10, src.m11, src.m13, src.m20, src.m21, src.m23, src.m30, src.m31, src.m33);
            float t03 = -determinant3x3(src.m10, src.m11, src.m12, src.m20, src.m21, src.m22, src.m30, src.m31,
                    src.m32);
            float t10 = -determinant3x3(src.m01, src.m02, src.m03, src.m21, src.m22, src.m23, src.m31, src.m32,
                    src.m33);
            float t11 = determinant3x3(src.m00, src.m02, src.m03, src.m20, src.m22, src.m23, src.m30, src.m32, src.m33);
            float t12 = -determinant3x3(src.m00, src.m01, src.m03, src.m20, src.m21, src.m23, src.m30, src.m31,
                    src.m33);
            float t13 = determinant3x3(src.m00, src.m01, src.m02, src.m20, src.m21, src.m22, src.m30, src.m31, src.m32);
            float t20 = determinant3x3(src.m01, src.m02, src.m03, src.m11, src.m12, src.m13, src.m31, src.m32, src.m33);
            float t21 = -determinant3x3(src.m00, src.m02, src.m03, src.m10, src.m12, src.m13, src.m30, src.m32,
                    src.m33);
            float t22 = determinant3x3(src.m00, src.m01, src.m03, src.m10, src.m11, src.m13, src.m30, src.m31, src.m33);
            float t23 = -determinant3x3(src.m00, src.m01, src.m02, src.m10, src.m11, src.m12, src.m30, src.m31,
                    src.m32);
            float t30 = -determinant3x3(src.m01, src.m02, src.m03, src.m11, src.m12, src.m13, src.m21, src.m22,
                    src.m23);
            float t31 = determinant3x3(src.m00, src.m02, src.m03, src.m10, src.m12, src.m13, src.m20, src.m22, src.m23);
            float t32 = -determinant3x3(src.m00, src.m01, src.m03, src.m10, src.m11, src.m13, src.m20, src.m21,
                    src.m23);
            float t33 = determinant3x3(src.m00, src.m01, src.m02, src.m10, src.m11, src.m12, src.m20, src.m21, src.m22);
            dest.m00 = t00 * determinant_inv;
            dest.m11 = t11 * determinant_inv;
            dest.m22 = t22 * determinant_inv;
            dest.m33 = t33 * determinant_inv;
            dest.m01 = t10 * determinant_inv;
            dest.m10 = t01 * determinant_inv;
            dest.m20 = t02 * determinant_inv;
            dest.m02 = t20 * determinant_inv;
            dest.m12 = t21 * determinant_inv;
            dest.m21 = t12 * determinant_inv;
            dest.m03 = t30 * determinant_inv;
            dest.m30 = t03 * determinant_inv;
            dest.m13 = t31 * determinant_inv;
            dest.m31 = t13 * determinant_inv;
            dest.m32 = t23 * determinant_inv;
            dest.m23 = t32 * determinant_inv;
            return dest;
        } else {
            return null;
        }
    }

    public Matrix negate() {
        return this.negate(this);
    }

    public Matrix4f negate(Matrix4f dest) {
        return negate(this, dest);
    }

    public static Matrix4f negate(Matrix4f src, Matrix4f dest) {
        if (dest == null) {
            dest = new Matrix4f();
        }

        dest.m00 = -src.m00;
        dest.m01 = -src.m01;
        dest.m02 = -src.m02;
        dest.m03 = -src.m03;
        dest.m10 = -src.m10;
        dest.m11 = -src.m11;
        dest.m12 = -src.m12;
        dest.m13 = -src.m13;
        dest.m20 = -src.m20;
        dest.m21 = -src.m21;
        dest.m22 = -src.m22;
        dest.m23 = -src.m23;
        dest.m30 = -src.m30;
        dest.m31 = -src.m31;
        dest.m32 = -src.m32;
        dest.m33 = -src.m33;
        return dest;
    }

    public Matrix4f translationRotateScale(float tx, float ty, float tz,
            float qx, float qy, float qz, float qw,
            float sx, float sy, float sz) {
        float dqx = qx + qx;
        float dqy = qy + qy;
        float dqz = qz + qz;
        float q00 = dqx * qx;
        float q11 = dqy * qy;
        float q22 = dqz * qz;
        float q01 = dqx * qy;
        float q02 = dqx * qz;
        float q03 = dqx * qw;
        float q12 = dqy * qz;
        float q13 = dqy * qw;
        float q23 = dqz * qw;
        return this
                ._m00(sx - (q11 + q22) * sx)
                ._m01((q01 + q23) * sx)
                ._m02((q02 - q13) * sx)
                ._m03(0.0f)
                ._m10((q01 - q23) * sy)
                ._m11(sy - (q22 + q00) * sy)
                ._m12((q12 + q03) * sy)
                ._m13(0.0f)
                ._m20((q02 + q13) * sz)
                ._m21((q12 - q03) * sz)
                ._m22(sz - (q11 + q00) * sz)
                ._m23(0.0f)
                ._m30(tx)
                ._m31(ty)
                ._m32(tz)
                ._m33(1.0f);
    }

    /**
     * Set the value of the matrix element at column 0 and row 0 without updating the properties of the matrix.
     *
     * @param m00 the new value
     * @return this
     */
    Matrix4f _m00(float m00) {
        this.m00 = m00;
        return this;
    }

    /**
     * Set the value of the matrix element at column 0 and row 1 without updating the properties of the matrix.
     *
     * @param m01 the new value
     * @return this
     */
    Matrix4f _m01(float m01) {
        this.m01 = m01;
        return this;
    }

    /**
     * Set the value of the matrix element at column 0 and row 2 without updating the properties of the matrix.
     *
     * @param m02 the new value
     * @return this
     */
    Matrix4f _m02(float m02) {
        this.m02 = m02;
        return this;
    }

    /**
     * Set the value of the matrix element at column 0 and row 3 without updating the properties of the matrix.
     *
     * @param m03 the new value
     * @return this
     */
    Matrix4f _m03(float m03) {
        this.m03 = m03;
        return this;
    }

    /**
     * Set the value of the matrix element at column 1 and row 0 without updating the properties of the matrix.
     *
     * @param m10 the new value
     * @return this
     */
    Matrix4f _m10(float m10) {
        this.m10 = m10;
        return this;
    }

    /**
     * Set the value of the matrix element at column 1 and row 1 without updating the properties of the matrix.
     *
     * @param m11 the new value
     * @return this
     */
    Matrix4f _m11(float m11) {
        this.m11 = m11;
        return this;
    }

    /**
     * Set the value of the matrix element at column 1 and row 2 without updating the properties of the matrix.
     *
     * @param m12 the new value
     * @return this
     */
    Matrix4f _m12(float m12) {
        this.m12 = m12;
        return this;
    }

    /**
     * Set the value of the matrix element at column 1 and row 3 without updating the properties of the matrix.
     *
     * @param m13 the new value
     * @return this
     */
    Matrix4f _m13(float m13) {
        this.m13 = m13;
        return this;
    }

    /**
     * Set the value of the matrix element at column 2 and row 0 without updating the properties of the matrix.
     *
     * @param m20 the new value
     * @return this
     */
    Matrix4f _m20(float m20) {
        this.m20 = m20;
        return this;
    }

    /**
     * Set the value of the matrix element at column 2 and row 1 without updating the properties of the matrix.
     *
     * @param m21 the new value
     * @return this
     */
    Matrix4f _m21(float m21) {
        this.m21 = m21;
        return this;
    }

    /**
     * Set the value of the matrix element at column 2 and row 2 without updating the properties of the matrix.
     *
     * @param m22 the new value
     * @return this
     */
    Matrix4f _m22(float m22) {
        this.m22 = m22;
        return this;
    }

    /**
     * Set the value of the matrix element at column 2 and row 3 without updating the properties of the matrix.
     *
     * @param m23 the new value
     * @return this
     */
    Matrix4f _m23(float m23) {
        this.m23 = m23;
        return this;
    }

    /**
     * Set the value of the matrix element at column 3 and row 0 without updating the properties of the matrix.
     *
     * @param m30 the new value
     * @return this
     */
    Matrix4f _m30(float m30) {
        this.m30 = m30;
        return this;
    }

    /**
     * Set the value of the matrix element at column 3 and row 1 without updating the properties of the matrix.
     *
     * @param m31 the new value
     * @return this
     */
    Matrix4f _m31(float m31) {
        this.m31 = m31;
        return this;
    }

    /**
     * Set the value of the matrix element at column 3 and row 2 without updating the properties of the matrix.
     *
     * @param m32 the new value
     * @return this
     */
    Matrix4f _m32(float m32) {
        this.m32 = m32;
        return this;
    }

    /**
     * Set the value of the matrix element at column 3 and row 3 without updating the properties of the matrix.
     *
     * @param m33 the new value
     * @return this
     */
    Matrix4f _m33(float m33) {
        this.m33 = m33;
        return this;
    }

    public Vector4f frustumPlane(int plane) {
        Vector4f dest = new Vector4f();
        switch (plane) {
            case 0: // PLANE_NX
                dest.set(m03 + m00, m13 + m10, m23 + m20, m33 + m30);
                dest.normalize3();
                break;
            case 1: // PLANE_PX
                dest.set(m03 - m00, m13 - m10, m23 - m20, m33 - m30);
                dest.normalize3();
                break;
            case 2: // PLANE_NY
                dest.set(m03 + m01, m13 + m11, m23 + m21, m33 + m31);
                dest.normalize3();
                break;
            case 3: // PLANE_PY
                dest.set(m03 - m01, m13 - m11, m23 - m21, m33 - m31);
                dest.normalize3();
                break;
            case 4: // PLANE_NZ
                dest.set(m03 + m02, m13 + m12, m23 + m22, m33 + m32);
                dest.normalize3();
                break;
            case 5: // PLANE_PZ
                dest.set(m03 - m02, m13 - m12, m23 - m22, m33 - m32);
                dest.normalize3();
                break;
            default:
                throw new IllegalArgumentException("dest");
        }
        return dest;
    }
}