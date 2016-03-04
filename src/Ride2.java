import javax.swing.*;
import java.awt.*;
import java.io.*;
import java.lang.reflect.Field;
import java.net.*;
import java.nio.charset.Charset;
import java.nio.file.*;
import java.awt.event.*;

public class Ride2 extends JFrame{
    static JTextField txtVon;
    static JTextField txtNach;
    static JLabel lblResult;
    static JLabel lblWeather;

    public Ride2() throws IOException{
        txtVon = new JTextField();
        txtNach = new JTextField();
        lblResult = new JLabel();
        lblWeather = new JLabel();

        setTitle("Ride2");
        setSize(417,339);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setVisible(true);
        setLayout(new BorderLayout());
        JLabel background=new JLabel(new ImageIcon("img/bg.png"));
        add(background);
        background.setLayout(null);

        txtVon.setBounds(165, 13, 195, 34);
        txtVon.setBorder(javax.swing.BorderFactory.createEmptyBorder());
        txtVon.setOpaque(false);
        txtVon.addKeyListener(new FocusListener());
        background.add(txtVon);

        txtNach.setBounds(165, 51, 195, 34);
        txtNach.setBorder(javax.swing.BorderFactory.createEmptyBorder());
        txtNach.setOpaque(false);
        txtNach.addKeyListener(new ExecListener());
        background.add(txtNach);

        lblResult.setBounds(146, 151, 230, 34);
        lblResult.setForeground(new Color(33, 67, 189));
        lblResult.setFont(new Font("Calibri", 3, 14));
        lblResult.setCursor(new Cursor(Cursor.HAND_CURSOR));
        lblResult.addMouseListener(new OpenListener());
        background.add(lblResult);

        lblWeather.setBounds(130, 149, 60, 34);
        lblWeather.setForeground(new Color(33, 67, 189));
        background.add(lblWeather);

        setSize(416,338);
        setSize(417,339);
    }

    public static void main(String[] args){
        System.setProperty("file.encoding","UTF-8");
        Field charset;
        try {
            charset = Charset.class.getDeclaredField("defaultCharset");
            charset.setAccessible(true);

            try {
                charset.set(null, null);
            }catch(IllegalAccessException exc){
                System.err.println(exc.getMessage());
            }
        }catch(NoSuchFieldException exc){
            System.err.println(exc.getMessage());
        }

        try{
            new Ride2();
        }catch(IOException exc){
            System.err.println(exc.getMessage());
        }
    }
}

class FocusListener implements KeyListener{
    public void keyTyped(KeyEvent e) {
    }

    public void keyPressed(KeyEvent e) {
        if(e.getKeyCode() == KeyEvent.VK_ENTER){
            Ride2.txtNach.grabFocus();
        }
    }

    public void keyReleased(KeyEvent e) {
    }
}

class ExecListener implements KeyListener{
    private URL url;
    private URLConnection conn;
    private InputStream inStream;
    private Path path;
    private byte[] data;
    private Coord c1;
    private Coord c2;
    private double temperature;
    private String cond;
    private String weather;

    public void keyTyped(KeyEvent e) {
    }

    public void keyPressed(KeyEvent e) {
        if(e.getKeyCode() == KeyEvent.VK_ENTER){
            path = Paths.get("result.txt");

            try(OutputStream out = new BufferedOutputStream(Files.newOutputStream(path))) {
                url = new URL("https://graphhopper.com/api/1/geocode?q=" + Ride2.txtVon.getText().trim() + "&debug=true&key=da0de59b-e670-491b-a312-a8088a50af69");
                conn = url.openConnection();
                inStream = ((InputStream)conn.getContent());
                data = new byte[inStream.available()];
                inStream.read(data);

                c1 = new Coord(Double.parseDouble(JParser.get(new String(data), "lng", 5)), Double.parseDouble(JParser.get(new String(data), "lat", 5)));

                url = new URL("https://graphhopper.com/api/1/geocode?q=" + Ride2.txtNach.getText().trim() + "&debug=true&key=da0de59b-e670-491b-a312-a8088a50af69");
                conn = url.openConnection();
                inStream = ((InputStream)conn.getContent());
                data = new byte[inStream.available()];
                inStream.read(data);

                c2 = new Coord(Double.parseDouble(JParser.get(new String(data), "lng", 5)), Double.parseDouble(JParser.get(new String(data), "lat", 5)));

                url = new URL("https://graphhopper.com/api/1/route?point=" + c1.getLat() + "%2C" + c1.getLon() + "&point=" + c2.getLat() + "%2C" + c2.getLon() + "&vehicle=car&locale=de&debug=true&points_encoded=false&key=da0de59b-e670-491b-a312-a8088a50af69");
                conn = url.openConnection();
                inStream = ((InputStream)conn.getContent());
                data = new byte[inStream.available()];
                inStream.read(data);

                for(String str : JParser.getList(new String(data), "text")){
                    if(str == null){
                        break;
                    }

                    byte[] line = str.getBytes();
                    out.write(line, 0, line.length);
                }

                url = new URL("https://api.forecast.io/forecast/6936bf071ba32e575b3516a2f3cb3306/" + c2.getLat() + "," + c2.getLon());
                conn = url.openConnection();
                inStream = ((InputStream)conn.getContent());
                data = new byte[inStream.available()];
                inStream.read(data);

                temperature = Double.parseDouble(JParser.get(new String(data), "temperature", 13));
                String temp = JParser.get(new String(data), "summary", 10);
                cond = temp.substring(0, temp.length() - 1);

                switch(cond){
                    case "clear": weather = "☀";
                                  break;
                    case "wind": weather = "☀";
                                  break;
                    default: weather = "☂";
                }

                Ride2.lblWeather.setText(weather);
                Ride2.lblResult.setText(Math.round((((temperature - 32) * 5) / 9) * 10) / 10 + "C. So kommen Sie nach " + Ride2.txtNach.getText() + '.');
            } catch (IOException exc) {
                System.err.println(exc);
            }
        }
    }

    public void keyReleased(KeyEvent e) {
    }
}

class OpenListener implements MouseListener{
    @Override
    public void mouseClicked(MouseEvent e) {
        ProcessBuilder pb = new ProcessBuilder("Notepad.exe", "result.txt");

        try {
            pb.start();
        }catch(IOException exc){
            System.err.println(exc);
        }
    }

    @Override
    public void mousePressed(MouseEvent e){
    }

    @Override
    public void mouseEntered(MouseEvent e){
    }

    @Override
    public void mouseExited(MouseEvent e){
    }

    @Override
    public void mouseReleased(MouseEvent e){
    }
}

class Coord{
    private double lon;
    private double lat;

    public Coord(double lon, double lat){
        this.lon = lon;
        this.lat = lat;
    }

    public double getLon(){
        return lon;
    }

    public double getLat(){
        return lat;
    }
}

class JParser{
    public static String get(String json, String item, int offset){
        String temp = json.substring(json.indexOf(item) + item.length());
        char end = temp.indexOf(',') < temp.indexOf('}') ? ',' : '}';
        String result = json.substring(json.indexOf(item) + offset, json.indexOf(item) + item.length() + temp.indexOf(end));
        return result;
    }

    public static String[] getList(String json, String item){
        String[] result = new String[8];
        String temp = json;
        int i = 0;

        while(true){
            try{
                temp = temp.substring(temp.indexOf(item) + item.length() + 4);
            }catch(IndexOutOfBoundsException exc){
                System.err.println(exc);
            }

            if(temp.indexOf(item) == -1){
                break;
            }

            if(result.length == i){
                result = resizeArr(result);
            }

            result[i] = temp.substring(0, temp.indexOf('\"')) + System.getProperty("line.separator");

            if(result[i].contains("Geradeaus")){
                result[i] = "↑ " + result[i];
            }else if(result[i].toLowerCase().contains("links")){
                result[i] = "← " + result[i];
            }else if(result[i].toLowerCase().contains("rechts")){
                result[i] = "→ " + result[i];
            }else if(result[i].contains("Kreisverkehr")){
                result[i] = "↺ " + result[i];
            }

            i++;
        }

        return result;
    }

    private static String[] resizeArr(String[] arr){
        String[] result = new String[arr.length * 2];

        for(int i = 0; i < arr.length; i++){
            result[i] = arr[i];
        }

        return result;
    }
}
