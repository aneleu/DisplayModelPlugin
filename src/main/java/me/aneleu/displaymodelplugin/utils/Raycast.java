package me.aneleu.displaymodelplugin.utils;

import org.bukkit.Location;
import org.bukkit.entity.BlockDisplay;
import org.bukkit.entity.Player;
import org.bukkit.util.Transformation;
import org.bukkit.util.Vector;
import org.joml.AxisAngle4d;

public class Raycast {

    private static final double[] yawAxis = new double[]{0, 1, 0};

    /* cube 배열
    0번째: 육면체의 아래 면의 왼쪽 앞 꼭짓점
    1번째: 육면체의 아래 면의 오른쪽 앞 꼭짓점
    2번째: 육면체의 아래 면의 오른쪽 뒤 꼭짓점
    3번째: 육면체의 아래 면의 왼쪽 뒤 꼭짓점
    4번째: 육면체의 위 면의 왼쪽 앞 꼭짓점
    5번째: 육면체의 위 면의 오른쪽 앞 꼭짓점
    6번째: 육면체의 위 면의 오른쪽 뒤 꼭짓점
    7번째: 육면체의 위 면의 왼쪽 뒤 꼭짓점
     */

    /**
     * 점에서 벡터 방향으로 연장한 반직선이 직육면체 큐브를 관통하는지 검사합니다.
     *
     * @param cube   큐브의 꼭짓점 좌표.
     * @param point  점
     * @param vector 벡터
     * @return 점에서 벡터 방향으로 연장한 반직선이 큐브를 지나는지 여부
     */
    public static boolean intersect(double[][] cube, double[] point, double[] vector) {

        int[][] faces = new int[][]{{0, 1, 5, 4}, {1, 2, 6, 5}, {2, 3, 7, 6}, {3, 0, 4, 7}, {0, 1, 2, 3}, {4, 5, 6, 7}};

        for (int[] face : faces) {
            double[] U = new double[]{cube[face[1]][0] - cube[face[0]][0], cube[face[1]][1] - cube[face[0]][1], cube[face[1]][2] - cube[face[0]][2]};
            double[] V = new double[]{cube[face[3]][0] - cube[face[0]][0], cube[face[3]][1] - cube[face[0]][1], cube[face[3]][2] - cube[face[0]][2]};

            double[] N = new double[]{U[1] * V[2] - U[2] * V[1], U[2] * V[0] - U[0] * V[2], U[0] * V[1] - U[1] * V[0]};

            double t = ((cube[face[0]][0] - point[0]) * N[0] + (cube[face[0]][1] - point[1]) * N[1] + (cube[face[0]][2] - point[2]) * N[2]) / (vector[0] * N[0] + vector[1] * N[1] + vector[2] * N[2]);
            if (t < 0) {
                continue;
            }

            double[] P = new double[]{point[0] + t * vector[0], point[1] + t * vector[1], point[2] + t * vector[2]};

            double[] C = new double[]{cube[face[0]][0] + U[0] + V[0], cube[face[0]][1] + U[1] + V[1], cube[face[0]][2] + U[2] + V[2]};
            if (Math.min(Math.min(cube[face[0]][0], cube[face[1]][0]), C[0]) <= P[0] && P[0] <= Math.max(Math.max(cube[face[0]][0], cube[face[1]][0]), C[0]) &&
                    Math.min(Math.min(cube[face[0]][1], cube[face[1]][1]), C[1]) <= P[1] && P[1] <= Math.max(Math.max(cube[face[0]][1], cube[face[1]][1]), C[1]) &&
                    Math.min(Math.min(cube[face[0]][2], cube[face[1]][2]), C[2]) <= P[2] && P[2] <= Math.max(Math.max(cube[face[0]][2], cube[face[1]][2]), C[2])) {
                return true;
            }
        }

        return false;
    }

    public static boolean isPlayerTargetingDisplay(Player player, BlockDisplay display) {
        Location eyeLocation = player.getEyeLocation();
        Vector eyeDirection = eyeLocation.getDirection();
        double[] point = {eyeLocation.getX(), eyeLocation.getY(), eyeLocation.getZ()};
        double[] direction = {eyeDirection.getX(), eyeDirection.getY(), eyeDirection.getZ()};

        // yaw / pitch 를 기준으로 회전시킨 두 점 구하기
        double[] displayLocation = {display.getX(), display.getY(), display.getZ()};
        double yawRad = Math.toRadians(display.getYaw());
        double pitchRad = Math.toRadians(display.getPitch());
        double[] pitchAxis = {-Math.cos(yawRad), 0, -Math.sin(yawRad)};

        double[] dot1 = Rotation.rotateDotByAxisAngle(point, displayLocation, pitchAxis, pitchRad);
        double[] vector1 = Rotation.rotateVectorByAxisAngle(direction, pitchAxis, pitchRad);

        double[] dot2 = Rotation.rotateDotByAxisAngle(dot1, displayLocation, yawAxis, yawRad);
        double[] vector2 = Rotation.rotateVectorByAxisAngle(vector1, yawAxis, yawRad);

        // y_rotation axis를 기준으로 회전시킨 두 점 구하기
        Transformation trans = display.getTransformation();
        AxisAngle4d axis_angle = new AxisAngle4d();
        trans.getLeftRotation().get(axis_angle);
        double x = display.getX() + trans.getTranslation().x;
        double y = display.getY() + trans.getTranslation().y;
        double z = display.getZ() + trans.getTranslation().z;
        double dx = trans.getScale().x;
        double dy = trans.getScale().y;
        double dz = trans.getScale().z;
        double[] origin = {x, y, z};
        double[] axis = {axis_angle.x, axis_angle.y, axis_angle.z};
        double theta = -axis_angle.angle;
        double[] pos1 = {x, y, z};
        double[] pos2 = {x + dx, y, z};
        double[] pos3 = {x + dx, y, z + dz};
        double[] pos4 = {x, y, z + dz};
        double[] pos5 = {x, y + dy, z};
        double[] pos6 = {x + dx, y + dy, z};
        double[] pos7 = {x + dx, y + dy, z + dz};
        double[] pos8 = {x, y + dy, z + dz};
        double[][] cube = {pos1, pos2, pos3, pos4, pos5, pos6, pos7, pos8};

        double[] resultDot = Rotation.rotateDotByAxisAngle(dot2, origin, axis, theta);
        double[] resultVector = Rotation.rotateVectorByAxisAngle(vector2, axis, theta);

        // 레이캐스트 결과
        return intersect(cube, resultDot, resultVector);

    }

}
