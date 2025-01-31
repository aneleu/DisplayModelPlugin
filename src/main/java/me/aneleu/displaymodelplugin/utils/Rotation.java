package me.aneleu.displaymodelplugin.utils;

import org.jetbrains.annotations.NotNull;

public class Rotation {

    private static final double[] origin = {0, 0, 0};

    /**
     * 점을 AxisAngle에 대해 회전했을때의 점 좌표를 구합니다.
     *
     * @param point  점의 좌표
     * @param origin 축 벡터의 시작점 좌표
     * @param axis   축 백터
     * @param angle  각도
     * @return 회전된 점의 좌표
     */
    public static double @NotNull [] rotateDotByAxisAngle(double[] point, double[] origin, double[] axis, double angle) {

        double u = axis[0];
        double v = axis[1];
        double w = axis[2];

        double magnitude = Math.sqrt(u * u + v * v + w * w);
        if (magnitude != 0) {
            u /= magnitude;
            v /= magnitude;
            w /= magnitude;
        } else {
            w = 1;
        }

        double x = point[0] - origin[0];
        double y = point[1] - origin[1];
        double z = point[2] - origin[2];

        double[] result = new double[3];
        double v1 = u * x + v * y + w * z;
        result[0] = (u * v1 * (1 - Math.cos(angle)) + x * Math.cos(angle) + (-w * y + v * z) * Math.sin(angle)) + origin[0];
        result[1] = (v * v1 * (1 - Math.cos(angle)) + y * Math.cos(angle) + (w * x - u * z) * Math.sin(angle)) + origin[1];
        result[2] = (w * v1 * (1 - Math.cos(angle)) + z * Math.cos(angle) + (-v * x + u * y) * Math.sin(angle)) + origin[2];

        return result;
    }

    /**
     * 점을 쿼터니언에 대해 회전했을때의 점 좌표를 구합니다.
     *
     * @param point      점의 좌표
     * @param origin     회전의 기준점 좌표
     * @param quaternion 쿼터니언 (w, x, y, z)
     * @return 회전된 점의 좌표
     */
    public static double @NotNull [] rotateDotByQuaternion(double[] point, double[] origin, double[] quaternion) {
        double q0 = quaternion[0];
        double q1 = quaternion[1];
        double q2 = quaternion[2];
        double q3 = quaternion[3];

        double magnitude = Math.sqrt(q0 * q0 + q1 * q1 + q2 * q2 + q3 * q3);
        if (magnitude != 0) {
            q0 /= magnitude;
            q1 /= magnitude;
            q2 /= magnitude;
            q3 /= magnitude;
        }

        double x = point[0] - origin[0];
        double y = point[1] - origin[1];
        double z = point[2] - origin[2];

        double[] result = new double[3];
        result[0] = (1 - 2 * (q2 * q2 + q3 * q3)) * x + 2 * (q1 * q2 - q0 * q3) * y + 2 * (q1 * q3 + q0 * q2) * z + origin[0];
        result[1] = 2 * (q1 * q2 + q0 * q3) * x + (1 - 2 * (q1 * q1 + q3 * q3)) * y + 2 * (q2 * q3 - q0 * q1) * z + origin[1];
        result[2] = 2 * (q1 * q3 - q0 * q2) * x + 2 * (q2 * q3 + q0 * q1) * y + (1 - 2 * (q1 * q1 + q2 * q2)) * z + origin[2];

        return result;
    }

    /**
     * 점을 Euler 각도에 대해 회전했을때의 점 좌표를 구합니다.
     *
     * @param point       점의 좌표
     * @param origin      회전의 기준점 좌표
     * @param eulerAngles Euler 각도 (yaw -> pitch)
     * @return 회전된 점의 좌표
     */
    public static double @NotNull [] rotateDotByEulerAngle(double[] point, double[] origin, double[] eulerAngles) {
        double yaw = eulerAngles[0];
        double pitch = eulerAngles[1];

        double x = point[0] - origin[0];
        double y = point[1] - origin[1];
        double z = point[2] - origin[2];

        // Rotation matrices for yaw and pitch
        double[][] yawMatrix = {
                {Math.cos(yaw), -Math.sin(yaw), 0},
                {Math.sin(yaw), Math.cos(yaw), 0},
                {0, 0, 1}
        };

        double[][] pitchMatrix = {
                {Math.cos(pitch), 0, Math.sin(pitch)},
                {0, 1, 0},
                {-Math.sin(pitch), 0, Math.cos(pitch)}
        };

        // Apply yaw and pitch rotations
        double[] result = new double[3];
        double[] temp = new double[3];

        // Apply yaw
        temp[0] = yawMatrix[0][0] * x + yawMatrix[0][1] * y + yawMatrix[0][2] * z;
        temp[1] = yawMatrix[1][0] * x + yawMatrix[1][1] * y + yawMatrix[1][2] * z;
        temp[2] = yawMatrix[2][0] * x + yawMatrix[2][1] * y + yawMatrix[2][2] * z;

        // Apply pitch
        x = temp[0];
        y = temp[1];
        z = temp[2];
        result[0] = pitchMatrix[0][0] * x + pitchMatrix[0][1] * y + pitchMatrix[0][2] * z;
        result[1] = pitchMatrix[1][0] * x + pitchMatrix[1][1] * y + pitchMatrix[1][2] * z;
        result[2] = pitchMatrix[2][0] * x + pitchMatrix[2][1] * y + pitchMatrix[2][2] * z;

        // Translate back to the original origin
        result[0] += origin[0];
        result[1] += origin[1];
        result[2] += origin[2];

        return result;
    }

    /**
     * 벡터를 AxisAngle에 대해 회전했을때의 벡터 좌표를 구합니다.
     *
     * @param vector 벡터
     * @param axis   Axis
     * @param angle  Angle
     * @return 회전된 벡터
     */
    public static double @NotNull [] rotateVectorByAxisAngle(double[] vector, double[] axis, double angle) {
        return rotateDotByAxisAngle(vector, origin, axis, angle);
    }

    /**
     * 벡터를 Euler 각도에 대해 회전했을때의 벡터 좌표를 구합니다.
     *
     * @param vector     벡터
     * @param eulerAngle 오일러각 (yaw -> pitch)
     * @return 회전된 벡터
     */
    public static double @NotNull [] rotateVectorByEulerAngle(double[] vector, double[] eulerAngle) {
        return rotateDotByEulerAngle(vector, origin, eulerAngle);
    }

}
