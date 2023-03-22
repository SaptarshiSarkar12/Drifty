package GUI;

public class test {
    public static void main(String[] args) {
// http://releases.ubuntu.com/22.10/ubuntu-22.10-desktop-amd64.iso
        int x = 2;
        int y = 50;
        int n = 0;
        do {
            n++;
            ++x;
            y -= x++;
        } while (x <= 10);
        System.out.println(y);
        System.out.println(n);
    }
}