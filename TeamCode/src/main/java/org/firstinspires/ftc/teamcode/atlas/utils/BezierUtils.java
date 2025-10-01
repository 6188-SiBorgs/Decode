package org.firstinspires.ftc.teamcode.atlas.utils;

import java.util.ArrayList;

public class BezierUtils {
    public static ArrayList<Vector2> sampleCubicBezier(Vector2 p0, Vector2 p1, Vector2 p2, Vector2 p3, double step) {
        ArrayList<Vector2> result = new ArrayList<>();

        double totalLength = approximateLength(p0, p1, p2, p3, 20);

        int numSteps = (int) Math.ceil(totalLength / step);
        double currentDistance = 0;

        for (int i = 1; i <= numSteps; i++) {
            double targetDist = i * step;
            double t = findTForDistance(p0, p1, p2, p3, targetDist, totalLength);
            result.add(bezierPoint(p0, p1, p2, p3, t));
        }

        return result;
    }

    private static double approximateLength(Vector2 p0, Vector2 p1, Vector2 p2, Vector2 p3, int subdivisions) {
        double length = 0;
        Vector2 prev = p0;
        for (int i = 1; i <= subdivisions; i++) {
            double t = i / (double) subdivisions;
            Vector2 pt = bezierPoint(p0, p1, p2, p3, t);
            length += prev.distance(pt);
            prev = pt;
        }
        return length;
    }

    private static double findTForDistance(Vector2 p0, Vector2 p1, Vector2 p2, Vector2 p3, double targetDist, double totalLength) {
        // Binary search over t
        double low = 0, high = 1;
        for (int i = 0; i < 15; i++) { // enough iterations for good precision
            double mid = (low + high) / 2;
            double dist = approximateLength(p0, p1, p2, p3, (int) (mid * 20) + 1);
            if (dist < targetDist) low = mid;
            else high = mid;
        }
        return (low + high) / 2;
    }

    private static Vector2 bezierPoint(Vector2 p0, Vector2 p1, Vector2 p2, Vector2 p3, double t) {
        double u = 1 - t;
        double tt = t * t;
        double uu = u * u;
        double uuu = uu * u;
        double ttt = tt * t;

        double x = uuu * p0.x
                + 3 * uu * t * p1.x
                + 3 * u * tt * p2.x
                + ttt * p3.x;

        double y = uuu * p0.y
                + 3 * uu * t * p1.y
                + 3 * u * tt * p2.y
                + ttt * p3.y;

        return new Vector2(x, y);
    }
}