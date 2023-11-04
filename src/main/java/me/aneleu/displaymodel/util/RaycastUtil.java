package me.aneleu.displaymodel.util;

public class RaycastUtil {

    /**
     * 점을 벡터에 대해 회전했을때의 점 좌표를 구합니다.
     * @param point 점의 좌표
     * @param origin 벡터의 시작점 좌표
     * @param axis 백터
     * @param theta 각도
     * @return 회전한 점의 좌표
     */
    public static double[] rotateDotByVector(double[] point, double[] origin, double[] axis, double theta) {

        double u = axis[0];
        double v = axis[1];
        double w = axis[2];

        double magnitude = Math.sqrt(u*u + v*v + w*w);
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
        result[0] = (u * v1 * (1 - Math.cos(theta)) + x*Math.cos(theta) + (-w*y + v*z)*Math.sin(theta)) + origin[0];
        result[1] = (v * v1 * (1 - Math.cos(theta)) + y*Math.cos(theta) + (w*x - u*z)*Math.sin(theta)) + origin[1];
        result[2] = (w * v1 * (1 - Math.cos(theta)) + z*Math.cos(theta) + (-v*x + u*y)*Math.sin(theta)) + origin[2];

        return result;
    }

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
     * 두 점을 이은 직선이 직육면체 큐브를 관통하는지 검사합니다.
     * @param cube 큐브의 꼭짓점 좌표. 
     * @param A 직선 위의 한 점
     * @param B 직선 위의 한 점
     * @return 직선이 큐브를 지나는지 여부
     */
    public static boolean intersects(double[][] cube, double[] A, double[] B) {
        double[] D = new double[]{B[0] - A[0], B[1] - A[1], B[2] - A[2]};

        int[][] faces = new int[][]{{0, 1, 5, 4}, {1, 2, 6, 5}, {2, 3, 7, 6}, {3, 0, 4, 7}, {0, 1, 2, 3}, {4, 5, 6, 7}};

        for (int[] face : faces) {
            double[] U = new double[]{cube[face[1]][0] - cube[face[0]][0], cube[face[1]][1] - cube[face[0]][1], cube[face[1]][2] - cube[face[0]][2]};
            double[] V = new double[]{cube[face[3]][0] - cube[face[0]][0], cube[face[3]][1] - cube[face[0]][1], cube[face[3]][2] - cube[face[0]][2]};

            double[] N = new double[]{U[1]*V[2] - U[2]*V[1], U[2]*V[0] - U[0]*V[2], U[0]*V[1] - U[1]*V[0]};

            double t = ((cube[face[0]][0] - A[0])*N[0] + (cube[face[0]][1] - A[1])*N[1] + (cube[face[0]][2] - A[2])*N[2]) / (D[0]*N[0] + D[1]*N[1] + D[2]*N[2]);
            double[] P = new double[]{A[0] + t*D[0], A[1] + t*D[1], A[2] + t*D[2]};

            double[] C = new double[]{cube[face[0]][0] + U[0] + V[0], cube[face[0]][1] + U[1] + V[1], cube[face[0]][2] + U[2] + V[2]};
            if (Math.min(Math.min(cube[face[0]][0], cube[face[1]][0]), C[0]) <= P[0] && P[0] <= Math.max(Math.max(cube[face[0]][0], cube[face[1]][0]), C[0]) &&
                    Math.min(Math.min(cube[face[0]][1], cube[face[1]][1]), C[1]) <= P[1] && P[1] <= Math.max(Math.max(cube[face[0]][1], cube[face[1]][1]), C[1]) &&
                    Math.min(Math.min(cube[face[0]][2], cube[face[1]][2]), C[2]) <= P[2] && P[2] <= Math.max(Math.max(cube[face[0]][2], cube[face[1]][2]), C[2])) {
                return true;
            }
        }

        return false;
    }

}
