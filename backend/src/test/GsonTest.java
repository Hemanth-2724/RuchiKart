import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public class GsonTest {
    static class TestMenu {
        private boolean isVeg;
        public boolean isVeg() { return isVeg; }
        public void setVeg(boolean veg) { isVeg = veg; }
    }

    public static void main(String[] args) {
        Gson gson = new GsonBuilder().create();
        TestMenu m = new TestMenu();
        m.setVeg(true);
        System.out.println("JSON output: " + gson.toJson(m));
        // If it prints {"veg":true} then field name IS stripped
        // If it prints {"isVeg":true} then field name is kept
    }
}
