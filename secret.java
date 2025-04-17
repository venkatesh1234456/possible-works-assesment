import org.json.JSONObject;
import org.json.JSONTokener;
import java.io.FileReader;
import java.util.*;

public class SecretReconstruction {

    public static class Point {
        int x, y;
        Point(int x, int y) {
            this.x = x;
            this.y = y;
        }
    }

    public static class ParsedInput {
        int n, k;
        List<Point> points;

        ParsedInput(int n, int k, List<Point> points) {
            this.n = n;
            this.k = k;
            this.points = points;
        }
    }

    public static ParsedInput parseInput(JSONObject jsonData) {
        int n = jsonData.getJSONObject("keys").getInt("n");
        int k = jsonData.getJSONObject("keys").getInt("k");

        List<Point> points = new ArrayList<>();

        for (String key : jsonData.keySet()) {
            if (key.equals("keys")) continue;

            int x = Integer.parseInt(key);
            JSONObject pointObj = jsonData.getJSONObject(key);
            int base = pointObj.getInt("base");
            String value = pointObj.getString("value");
            int y = Integer.parseInt(value, base);

            points.add(new Point(x, y));
        }

        points.sort(Comparator.comparingInt(p -> p.x));

        return new ParsedInput(n, k, points);
    }

    public static int lagrangeInterpolation(List<Point> points, int k) {
        double secret = 0;

        for (int j = 0; j < k; j++) {
            int xj = points.get(j).x;
            int yj = points.get(j).y;
            double numerator = 1;
            double denominator = 1;

            for (int m = 0; m < k; m++) {
                if (m != j) {
                    int xm = points.get(m).x;
                    numerator *= -xm;
                    denominator *= (xj - xm);
                }
            }

            double lj = numerator / denominator;
            secret += yj * lj;
        }

        return (int) Math.round(secret);
    }

    public static int findSecretFromJSONFile(String filePath) {
        try (FileReader reader = new FileReader(filePath)) {
            JSONObject jsonData = new JSONObject(new JSONTokener(reader));
            ParsedInput input = parseInput(jsonData);
            return lagrangeInterpolation(input.points, input.k);
        } catch (Exception e) {
            e.printStackTrace();
            return -1; // error code
        }
    }

    public static void main(String[] args) {
        int secret1 = findSecretFromJSONFile("input1.json");
        int secret2 = findSecretFromJSONFile("input2.json");
        System.out.println(secret1 + " " + secret2);
    }
}
