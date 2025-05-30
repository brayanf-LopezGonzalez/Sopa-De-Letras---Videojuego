package package1;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.geom.Path2D;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.io.FileWriter;
import java.io.Reader;
import java.io.Writer;
import java.lang.reflect.Type;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;

public class sopaDeLetras{
	
	private static JFrame frame;
    private static JPanel panelPrincipal;
    private static boolean musicaActivada = true;
    private static Sonido musicaFondo;
    static Timer cronometro;
    private static int segundosTranscurridos = 0;
    private static JLabel lblTiempo;
    private static int corazones = 3;
    
    public static final String[] palabras = {
    	    "gato", "perro", "casa", "arbol", "nube", "luz", "sol", "luna", "mar", "cielo",
    	    "estrella", "rio", "montana", "flor", "hoja", "roca", "viento", "fuego", "agua", "tierra",
    	    "bosque", "nieve", "hielo", "lluvia", "trueno", "relampago", "noche", "dia", "tarde", "amanecer",
    	    "atardecer", "invierno", "verano", "primavera", "otono", "camino", "sendero", "puente", "playa", "arena",
    	    "barco", "pez", "pajaro", "abeja", "mariposa", "tigre", "leon", "mono", "zorro", "serpiente",
    	    "caballo", "rana", "raton", "murcielago", "elefante", "jirafa", "ballena", "delfin", "pulpo", "cangrejo",
    	    "piedra", "tronco", "copa", "vino", "queso", "pan", "miel", "azucar", "sal", "pimienta",
    	    "fresa", "manzana", "pera", "platano", "uva", "naranja", "limon", "melocoton", "sandia", "melon",
    	    "zapato", "camisa", "pantalon", "sombrero", "abrigo", "guante", "bufanda", "reloj", "gafas", "mochila",
    	    "libro", "lapiz", "hoja", "cuaderno", "mesa", "silla", "puerta", "ventana", "cortina", "alfombra"
    };
    
    public static final int tamanoTablero = 12;
    private static char[][] tablero = new char[tamanoTablero][tamanoTablero];
    private static JButton[][] botonesTablero;
    private static List<String> palabrasNivelActual;
    private static List<JLabel> etiquetasPalabras = new ArrayList<>();

    public static void main(String[] args) {
        frame = new JFrame("Sopa de Letras");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(800, 600);
        frame.setLocationRelativeTo(null);
        
        musicaFondo = new Sonido("sounds/Pigstep.wav");
        if (musicaActivada) musicaFondo.reproducirLoop();

        mostrarMenu();
        frame.setVisible(true);
    }
    
    private static void mostrarMenu() {
        panelPrincipal = new JPanel(new BorderLayout()) {
        	Image fondo = new ImageIcon("images/fondo 1.jpg").getImage();
        	
        	@Override
        	protected void paintComponent(Graphics g) {
        		super.paintComponent(g);
        		Graphics2D g2d = (Graphics2D) g;
        		g2d.drawImage(fondo, 0, 0, getWidth(), getHeight(), this);
        	}
        };
        panelPrincipal.setBorder(BorderFactory.createEmptyBorder(50, 50, 50, 50));

        JLabel titulo = new JLabel("SOPA DE LETRAS", SwingConstants.CENTER);
        titulo.setFont(new Font("Arial", Font.BOLD, 48));
        panelPrincipal.add(titulo, BorderLayout.NORTH);

        JPanel panelBotones = new JPanel(new GridLayout(2, 1, 0, 30));
        panelBotones.setOpaque(false);
        panelBotones.setBorder(BorderFactory.createEmptyBorder(100, 150, 100, 150));

        JButton btnIniciarJuego = new JButton("Iniciar Juego");
        disenoBotones(btnIniciarJuego);
        btnIniciarJuego.addActionListener(e -> iniciarJuego());

        JButton btnMarcadores = new JButton("Marcadores");
        disenoBotones(btnMarcadores);
        btnMarcadores.addActionListener(e -> mostrarMarcadores());

        panelBotones.add(btnIniciarJuego);
        panelBotones.add(btnMarcadores);
        panelPrincipal.add(panelBotones, BorderLayout.CENTER);       

        JCheckBox toggleMusica = new JCheckBox("Musica de fondo");
        toggleMusica.setSelected(musicaActivada);
        toggleMusica.setOpaque(false);
        toggleMusica.addActionListener(e -> {
            musicaActivada = toggleMusica.isSelected();
            if (musicaActivada) {
                musicaFondo.reproducirLoop();
            } else {
                musicaFondo.detener();
            }
        });
        
        JPanel panelMusica = new JPanel();
        panelMusica.setOpaque(false);
        panelMusica.add(toggleMusica);
        panelPrincipal.add(panelMusica, BorderLayout.SOUTH);

        frame.getContentPane().removeAll();
        frame.add(panelPrincipal);
        frame.revalidate();
        frame.repaint();
    }

	private static void disenoBotones(JButton boton) {
		boton.setFont(new Font("Arial", Font.PLAIN, 24));
		boton.setBackground(new Color(70, 130, 180));
		boton.setForeground(Color.WHITE);
		boton.setFocusPainted(false);
		boton.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));
	}

	private static void iniciarJuego() {
		
		JTextField nombreJugador = new JTextField();
		nombreJugador.setDocument(new javax.swing.text.PlainDocument() {//Esto sirve para restringir a 10 caracteres ek nombre
	        @Override
	        public void insertString(int offset, String str, javax.swing.text.AttributeSet attr) throws javax.swing.text.BadLocationException {
	            if (str == null || getLength() + str.length() > 10) return;
	            super.insertString(offset, str, attr);
	        }
	    });

	    int opcion = JOptionPane.showConfirmDialog(frame,nombreJugador,"Ingrese un nombre (max 10 caracteres):",JOptionPane.OK_CANCEL_OPTION,JOptionPane.PLAIN_MESSAGE);

	    if (opcion == JOptionPane.OK_OPTION) {
	    	
	        String nombre = nombreJugador.getText().trim();
	        
	        if (nombre.isEmpty()) {
	        	JOptionPane.showMessageDialog(frame, "Por favor ingrese un nombre valido");
	        	iniciarJuego();
	        } else {
	           panelJuego(nombre);
	        }
	    }
	}

	public static class GestorMarcadores {

	    private static final String RUTA_JSON = "resources/marcadores.json";

	    public static List<Jugador> cargarJugadores() {
	        try (Reader reader = new FileReader(RUTA_JSON)) {
	            Type listType = new TypeToken<List<Jugador>>() {}.getType();
	            List<Jugador> jugadores = new Gson().fromJson(reader, listType);
	            return (jugadores != null) ? jugadores : new ArrayList<>();
	        } catch (Exception e) {
	            e.printStackTrace();
	            return new ArrayList<>();
	        }
	    }

	    public static void guardarJugador(Jugador jugador) {
	        List<Jugador> jugadores = cargarJugadores();
	        jugadores.add(jugador);

	        try (Writer writer = new FileWriter(RUTA_JSON)) {
	            new Gson().toJson(jugadores, writer);
	        } catch (Exception e) {
	            e.printStackTrace();
	        }
	    }
	}
	
	private static void mostrarMarcadores() {
		
	    List<Jugador> jugadores = GestorMarcadores.cargarJugadores();

	    JPanel panelMarcadores = new JPanel(new BorderLayout(20, 20));
	    panelMarcadores.setBorder(BorderFactory.createEmptyBorder(30, 30, 30, 30));
	    panelMarcadores.setBackground(new Color(200, 220, 240));

	    JLabel titulo = new JLabel("MARCADORES", SwingConstants.CENTER);
	    titulo.setFont(new Font("Arial", Font.BOLD, 32));
	    panelMarcadores.add(titulo, BorderLayout.NORTH);

	    String[] columnas = {"Nombre", "Puntaje", "Tiempo"};

	    DefaultTableModel modelo = new DefaultTableModel(columnas, 0);

	    for (Jugador j : jugadores) {
	        Object[] fila = {j.getNombre(), j.getPuntaje(), j.getTiempo()};
	        modelo.addRow(fila);
	    }

	    JTable tabla = new JTable(modelo);
	    tabla.setFont(new Font("Arial", Font.PLAIN, 16));
	    tabla.setRowHeight(25);
	    tabla.getTableHeader().setFont(new Font("Arial", Font.BOLD, 16));
	    tabla.setBackground(Color.WHITE);
	    tabla.setForeground(Color.BLACK);
	    tabla.getTableHeader().setBackground(new Color(100, 130, 180));
	    tabla.getTableHeader().setForeground(Color.WHITE);
	    
	    DefaultTableCellRenderer centrado = new DefaultTableCellRenderer();
	    centrado.setHorizontalAlignment(SwingConstants.CENTER);
	    
	    for (int i = 0; i < tabla.getColumnCount(); i++) {
	        tabla.getColumnModel().getColumn(i).setCellRenderer(centrado);
	    }
	    
	    JScrollPane scroll = new JScrollPane(tabla);
	    panelMarcadores.add(scroll, BorderLayout.CENTER);

	    JButton btnVolver = new JButton("Volver al Menu");
	    disenoBotones(btnVolver);
	    btnVolver.addActionListener(e -> mostrarMenu());
	    JPanel panelBoton = new JPanel();
	    panelBoton.setOpaque(false);
	    panelBoton.add(btnVolver);
	    panelMarcadores.add(panelBoton, BorderLayout.SOUTH);

	    frame.getContentPane().removeAll();
	    frame.add(panelMarcadores);
	    frame.revalidate();
	    frame.repaint();
	}
	
	public static class Jugador {
		
	    private String nombre;
	    private int puntaje;
	    private String tiempo;

	    public Jugador(String nombre, int puntaje, String tiempo) {
	        this.nombre = nombre;
	        this.puntaje = puntaje;
	        this.tiempo = tiempo;
	    }

	    public String getNombre() { return nombre; }
	    public int getPuntaje() { return puntaje; }
	    public String getTiempo() { return tiempo; }
	}
	
	public static class Sonido {
		
	    private Clip clip;

	    public Sonido(String ruta) {
	        try {
	            AudioInputStream audioIn = AudioSystem.getAudioInputStream(new File(ruta));
	            clip = AudioSystem.getClip();
	            clip.open(audioIn);
	        } catch (Exception e) {
	            e.printStackTrace();
	        }
	    }

	    public void reproducirLoop() {
	    	
	        if (clip != null) {
	            clip.loop(Clip.LOOP_CONTINUOUSLY);
	        }
	    }

	    public void reproducirUnaVez() {
	    	
	        if (clip != null) {
	            clip.setFramePosition(0);
	            clip.start();
	        }
	    }

	    public void detener() {
	    	
	        if (clip != null && clip.isRunning()) {
	            clip.stop();
	        }
	    }

	    public boolean sonando() {
	    	
	        return clip != null && clip.isRunning();
	    }
	}
	
	private static void panelJuego(String nombreJugador) {
		
        JPanel panelJuego = new JPanel(new BorderLayout());
        panelJuego.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        panelJuego.setBackground(Color.WHITE);

        JPanel panelSuperior = new JPanel(new BorderLayout());
        panelSuperior.setOpaque(false);

        JPanel panelCorazones = new JPanel();
        panelCorazones.setOpaque(false);
        panelCorazones.setLayout(new FlowLayout(FlowLayout.LEFT, 10, 10));

        for (int i = 0; i < 3; i++) {
            String ruta = (i < corazones) ? "images/corazonLleno.png" : "images/corazonVacio.png";
            try {
                ImageIcon icono = new ImageIcon(ruta);
                Image imagen = icono.getImage().getScaledInstance(30, 30, Image.SCALE_SMOOTH);
                JLabel lblCorazon = new JLabel(new ImageIcon(imagen));
                panelCorazones.add(lblCorazon);
            } catch (Exception e) {
                JLabel lblError = new JLabel("?");
                panelCorazones.add(lblError);
            }
        }

        panelSuperior.add(panelCorazones, BorderLayout.WEST);

        JPanel panelDerecho = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 10));
        panelDerecho.setOpaque(false);

        lblTiempo = new JLabel("Tiempo: 00:00");
        lblTiempo.setFont(new Font("Arial", Font.PLAIN, 18));
        lblTiempo.setForeground(Color.BLACK);

        JLabel lblNombre = new JLabel("Jugador: " + nombreJugador);
        lblNombre.setFont(new Font("Arial", Font.BOLD, 18));

        JLabel lblPuntaje = new JLabel("Puntaje: 0");
        lblPuntaje.setFont(new Font("Arial", Font.BOLD, 18));

        panelDerecho.add(lblTiempo);
        panelDerecho.add(lblNombre);
        panelDerecho.add(lblPuntaje);

        panelSuperior.add(panelDerecho, BorderLayout.CENTER);
        panelJuego.add(panelSuperior, BorderLayout.NORTH);
        
        JPanel panelPalabras = new JPanel();
        panelPalabras.setLayout(new BoxLayout(panelPalabras, BoxLayout.Y_AXIS));
        panelPalabras.setPreferredSize(new Dimension(180, 0));
        panelPalabras.setBackground(new Color(240, 240, 240));
        panelPalabras.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        etiquetasPalabras.clear();
        
        JLabel tituloPalabras = new JLabel("BUSCA:");
        tituloPalabras.setFont(new Font("Arial", Font.BOLD, 14));
        tituloPalabras.setAlignmentX(Component.CENTER_ALIGNMENT);
        tituloPalabras.setBorder(BorderFactory.createEmptyBorder(0, 0, 10, 0));
        panelPalabras.add(tituloPalabras);

        JPanel panelPalabrasGrid = new JPanel(new GridLayout(0, 2, 10, 5));
        panelPalabrasGrid.setBackground(new Color(240, 240, 240));
        panelPalabrasGrid.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
        
        palabrasNivelActual = Arrays.asList(Arrays.copyOfRange(palabras, 0, 10));

        for (String palabra : palabrasNivelActual) {
            JLabel lbl = new JLabel(palabra.toUpperCase());
            lbl.setFont(new Font("Arial", Font.PLAIN, 14));
            lbl.setForeground(Color.BLUE.darker());
            lbl.setBorder(BorderFactory.createEmptyBorder(3, 10, 3, 3));
            etiquetasPalabras.add(lbl);
            panelPalabrasGrid.add(lbl);
        }
        
        panelPalabras.add(panelPalabrasGrid);

        JPanel panelTablero = new JPanel(new GridLayout(tamanoTablero, tamanoTablero, 1, 1));
        panelTablero.setBackground(Color.BLACK);
        
        int tamanoCelda = 30;
        int anchoTablero = tamanoTablero * tamanoCelda;
        int altoTablero = tamanoTablero * tamanoCelda;
        panelTablero.setPreferredSize(new Dimension(anchoTablero, altoTablero));

        botonesTablero = new JButton[tamanoTablero][tamanoTablero];
        llenarTableroConLetras();

        for (int fila = 0; fila < tamanoTablero; fila++) {
            for (int col = 0; col < tamanoTablero; col++) {
                JButton btn = new JButton(String.valueOf(tablero[fila][col]));
                btn.setFont(new Font("Arial", Font.BOLD, 14));
                btn.setForeground(Color.DARK_GRAY);
                btn.setBackground(Color.WHITE);
                btn.setFocusable(false);
                btn.setMargin(new Insets(0, 0, 0, 0));
                btn.setPreferredSize(new Dimension(tamanoCelda, tamanoCelda));
                botonesTablero[fila][col] = btn;
                panelTablero.add(btn);
            }
        }
        
        JPanel panelInferior = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        panelInferior.setOpaque(false);
        
        JButton btnRegresar = new JButton("Regresar al Menu");
        disenoBotones(btnRegresar);
        btnRegresar.addActionListener(e -> {
            guardarDatosJugador(nombreJugador);
            mostrarMenu();
        });
        
        panelInferior.add(btnRegresar);

        panelJuego.add(panelSuperior, BorderLayout.NORTH);
        panelJuego.add(panelTablero, BorderLayout.CENTER);
        panelJuego.add(panelPalabras, BorderLayout.EAST);
        panelJuego.add(panelInferior, BorderLayout.SOUTH);

        frame.setMinimumSize(new Dimension(800, 600));
        frame.pack();

        iniciarTemporizador();

        frame.getContentPane().removeAll();
        frame.add(panelJuego);
        frame.revalidate();
        frame.repaint();
    }
	
	private static void iniciarTemporizador() {
	    segundosTranscurridos = 0;
	    lblTiempo.setText("Tiempo: 00:00");

	    cronometro = new Timer(1000, e -> {
	        segundosTranscurridos++;
	        int minutos = segundosTranscurridos / 60;
	        int segundos = segundosTranscurridos % 60;
	        lblTiempo.setText(String.format("Tiempo: %02d:%02d", minutos, segundos));
	    });

	    cronometro.start();
	}
	
	private static void llenarTableroConLetras() {
        Random rand = new Random();

        for (int i = 0; i < tamanoTablero; i++) {
            for (int j = 0; j < tamanoTablero; j++) {
                tablero[i][j] = (char) ('A' + rand.nextInt(26));
            }
        }

        for (String palabra : palabrasNivelActual) {
            int fila = rand.nextInt(tamanoTablero);
            int colInicio = rand.nextInt(tamanoTablero - palabra.length());

            for (int i = 0; i < palabra.length(); i++) {
                tablero[fila][colInicio + i] = Character.toUpperCase(palabra.charAt(i));
            }
        }
    }
	
	private static void guardarDatosJugador(String nombreJugador) {

	    if (cronometro != null) {
	        cronometro.stop();
	    }

	    String tiempoJugado = lblTiempo.getText().replace("Tiempo: ", "");

	    int palabrasEncontradas = (int) etiquetasPalabras.stream().filter(lbl -> lbl.getFont().getStyle() == Font.ITALIC).count();
	    int puntuacion = palabrasEncontradas * 10;

	    Jugador jugador = new Jugador(nombreJugador, puntuacion, tiempoJugado);
	    GestorMarcadores.guardarJugador(jugador);

	    JOptionPane.showMessageDialog(frame, "Datos guardados:\n" + "Nombre: " + nombreJugador + "\n" + "Puntuacion: " + puntuacion + "\n" + "Tiempo: " + tiempoJugado, "Regresando al menu",JOptionPane.INFORMATION_MESSAGE);
	}
	 
}
