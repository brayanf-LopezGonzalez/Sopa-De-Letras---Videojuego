package package1;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
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
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;
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
    private static Clip clipSonido = null;
    private static boolean sonidoActivado = true;
    static Timer cronometro;
    private static int segundosTranscurridos = 0;
    private static JLabel lblTiempo;
    private static int corazones = 3;
    private static int puntuacion = 0;
    private static JLabel lblPuntaje;
    private static String nombreJugadorActual;
    private static JPanel panelCorazones;
    private static boolean cronometroIniciado = false;
    
    private static Point puntoInicioSeleccion = null;
    private static Point puntoFinSeleccion = null;
    private static Rectangle areaSeleccion = null;
    
    public static final String[] palabras = {
    	    "gato", "perro", "casa", "arbol", "nube", "luz", "sol", "luna", "mar", "cielo",
    	    "estrella", "rio", "montana", "flor", "hoja", "roca", "viento", "fuego", "agua", "tierra",
    	    "bosque", "nieve", "hielo", "lluvia", "trueno", "relampago", "noche", "dia", "tarde", "amanecer",
    	    "atardecer", "invierno", "verano", "primavera", "otono", "camino", "sendero", "puente", "playa", "arena",
    	    "barco", "pez", "pajaro", "abeja", "itzel", "mariposa", "tigre", "leon", "mono", "zorro", "serpiente",
    	    "caballo", "rana", "raton", "murcielago", "elefante", "jirafa", "ballena", "delfin", "pulpo", "cangrejo",
    	    "piedra", "tronco", "copa", "vino", "queso", "pan", "miel", "azucar", "sal", "pimienta",
    	    "fresa", "manzana", "pera", "platano", "uva", "naranja", "limon", "melocoton", "sandia", "melon",
    	    "zapato", "camisa", "pantalon", "sombrero", "abrigo", "guante", "bufanda", "reloj", "gafas", "mochila",
    	    "libro", "lapiz", "hoja", "cuaderno", "mesa", "silla", "puerta", "ventana", "cortina", "alfombra"
    };
    
    private static final int[][] DIRECCIONES = {
    	    {0, 1},   // derecha
    	    {0, -1},  // izquierda
    	    {1, 0},   // abajo
    	    {-1, 0},  // arriba
    	    {1, 1},   // diagonal abajo derecha
    	    {-1, -1}, // diagonal arriba izquierda
    	    {-1, 1},  // diagonal arriba derecha
    	    {1, -1}   // diagonal abajo izquierda
    	};
    
    public static final int tamanoTablero = 12;
    private static char[][] tablero = new char[tamanoTablero][tamanoTablero];
    private static JButton[][] botonesTablero;
    private static List<String> palabrasNivelActual;
    private static List<JLabel> etiquetasPalabras = new ArrayList<>();
    
    private static boolean seleccionando = false;
    private static Set<Point> puntosSeleccionados = new HashSet<>();
    private static List<JLabel> etiquetasSeleccionadas = new ArrayList<>();
    private static final Set<JLabel> etiquetasCorrectas = new HashSet<>();
    
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
        
        JCheckBox chkSonido = new JCheckBox("Efectos de sonido");
        chkSonido.setSelected(sonidoActivado);
        chkSonido.setOpaque(false);
        chkSonido.addActionListener(e -> {
            sonidoActivado = chkSonido.isSelected();
        });
        
        JPanel panelMusica = new JPanel();
        panelMusica.setOpaque(false);
        panelMusica.setLayout(new BorderLayout());
        panelMusica.add(toggleMusica, BorderLayout.WEST);
        panelMusica.add(chkSonido, BorderLayout.EAST);
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
	           puntuacion = 0;
	           corazones = 3;
	           actualizarCorazones();
	           iniciarTemporizador();
	           
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
		
		nombreJugadorActual = nombreJugador;
		
        JPanel panelJuego = new JPanel(new BorderLayout());
        panelJuego.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        panelJuego.setBackground(Color.WHITE);

        JPanel panelSuperior = new JPanel(new BorderLayout());
        panelSuperior.setOpaque(false);

        panelCorazones = new JPanel();
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
        actualizarCorazones();
        panelSuperior.add(panelCorazones, BorderLayout.WEST);

        JPanel panelDerecho = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 10));
        panelDerecho.setOpaque(false);

        lblTiempo = new JLabel("Tiempo: " + puntuacion);
        lblTiempo.setFont(new Font("Arial", Font.PLAIN, 18));
        lblTiempo.setForeground(Color.BLACK);

        JLabel lblNombre = new JLabel("Jugador: " + nombreJugador);
        lblNombre.setFont(new Font("Arial", Font.BOLD, 18));

        lblPuntaje = new JLabel("Puntaje: " + puntuacion);
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
        
        palabrasNivelActual = new ArrayList<>();
        
        for (int i = 0; i < palabras.length; i++) {
            palabras[i] = palabras[i].toUpperCase();
        }
        
        insertarPalabrasEnTablero();
        
        JLabel tituloPalabras = new JLabel("BUSCA:");
        tituloPalabras.setFont(new Font("Arial", Font.BOLD, 15));
        tituloPalabras.setAlignmentX(Component.CENTER_ALIGNMENT);
        tituloPalabras.setBorder(BorderFactory.createEmptyBorder(0, 0, 10, 0));
        tituloPalabras.setPreferredSize(new Dimension(150, 25));
        tituloPalabras.setMaximumSize(new Dimension(150, 25));
        panelPalabras.add(tituloPalabras);

        for (String palabra : palabrasNivelActual) {
        	
            JLabel etiqueta = new JLabel("<html><div style='width:150px;'>" + palabra + "</div></html>");
            etiqueta.setFont(new Font("Arial", Font.PLAIN, 16));
            etiqueta.setAlignmentX(Component.LEFT_ALIGNMENT);
            etiqueta.setMaximumSize(new Dimension(150, 30));
            
            etiquetasPalabras.add(etiqueta);
            
            panelPalabras.add(etiqueta);
        }
        
        panelPalabras.revalidate();
        panelPalabras.repaint();

        JPanel panelTablero = new JPanel(new GridLayout(tamanoTablero, tamanoTablero, 1, 1));
        panelTablero.setBackground(Color.BLACK);
        
        int tamanoCelda = 30;
        int anchoTablero = tamanoTablero * tamanoCelda;
        int altoTablero = tamanoTablero * tamanoCelda;
        panelTablero.setPreferredSize(new Dimension(anchoTablero, altoTablero));

        for (int fila = 0; fila < tamanoTablero; fila++) {
            for (int col = 0; col < tamanoTablero; col++) {
                JLabel lbl = new JLabel("" + tablero[fila][col], SwingConstants.CENTER);
                lbl.setOpaque(true);
                lbl.setBackground(Color.WHITE);
                lbl.setFont(new Font("Arial", Font.BOLD, 20));
                lbl.setBorder(BorderFactory.createLineBorder(Color.GRAY));
                lbl.putClientProperty("fila", fila);
                lbl.putClientProperty("col", col);

                panelTablero.add(lbl);
            }
        }
        
        panelTablero.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                puntoInicioSeleccion = e.getPoint();
                puntoFinSeleccion = puntoInicioSeleccion;
                areaSeleccion = new Rectangle();
                panelTablero.repaint();
            }

            @Override
            public void mouseReleased(MouseEvent e) {
                try {
                	puntoFinSeleccion = e.getPoint();
                    calcularAreaSeleccion();
                    seleccionarLetrasEnLinea(panelTablero);
                    areaSeleccion = null;
                    verificarPalabraSeleccionada();
                } catch (Exception ex) {
                    ex.printStackTrace();
                    JOptionPane.showMessageDialog(frame, "Error: " + ex.getMessage());
                }
            }
        });

        panelTablero.addMouseMotionListener(new MouseMotionAdapter() {
            @Override
            public void mouseDragged(MouseEvent e) {
            	try {
                    puntoFinSeleccion = e.getPoint();
                    calcularAreaSeleccion();
                    panelTablero.repaint();
                } catch (Exception ex) {
                    ex.printStackTrace();
                    JOptionPane.showMessageDialog(frame, "Error: " + ex.getMessage());
                }
            }
        });
 
        JPanel panelInferior = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        panelInferior.setOpaque(false);
        
        JButton btnRegresar = new JButton("Regresar al Menu");
        disenoBotones(btnRegresar);
        btnRegresar.addActionListener(e -> {
            guardarDatosJugador(nombreJugadorActual);
            mostrarMenu();
        });
        
        panelInferior.add(btnRegresar);

        panelJuego.add(panelSuperior, BorderLayout.NORTH);
        panelJuego.add(panelTablero, BorderLayout.CENTER);
        panelJuego.add(panelPalabras, BorderLayout.EAST);
        panelJuego.add(panelInferior, BorderLayout.SOUTH);

        frame.setMinimumSize(new Dimension(800, 600));
        frame.pack();

        if (!cronometroIniciado) {
            iniciarTemporizador();
            cronometroIniciado = true;
        }

        frame.getContentPane().removeAll();
        frame.add(panelJuego);
        frame.revalidate();
        frame.repaint();
    }
	
	private static void iniciarTemporizador() {
		
	    lblTiempo.setText("Tiempo: 00:00");

	    cronometro = new Timer(1000, e -> {
	        segundosTranscurridos++;
	        int minutos = segundosTranscurridos / 60;
	        int segundos = segundosTranscurridos % 60;
	        lblTiempo.setText(String.format("Tiempo: %02d:%02d", minutos, segundos));
	    });

	    cronometro.start();
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
	
	private static boolean colocarPalabra(char[][] tablero, String palabra, int fila, int col, int[] direccion) {
	    int dx = direccion[0];
	    int dy = direccion[1];
	    int len = palabra.length();

	    int endRow = fila + dx * (len - 1);
	    int endCol = col + dy * (len - 1);

	    if (endRow < 0 || endRow >= tamanoTablero || endCol < 0 || endCol >= tamanoTablero) {
	        return false;
	    }

	    for (int i = 0; i < len; i++) {
	        int r = fila + dx * i;
	        int c = col + dy * i;
	        if (tablero[r][c] != '\0' && tablero[r][c] != palabra.charAt(i)) {
	            return false;
	        }
	    }

	    for (int i = 0; i < len; i++) {
	        int r = fila + dx * i;
	        int c = col + dy * i;
	        tablero[r][c] = palabra.charAt(i);
	    }

	    return true;
	}

	private static void insertarPalabrasEnTablero() {
	    tablero = new char[tamanoTablero][tamanoTablero];
	    palabrasNivelActual = new ArrayList<>();

	    Random random = new Random();
	    List<String> listaPalabras = new ArrayList<>(Arrays.asList(palabras));
	    Collections.shuffle(listaPalabras);

	    int palabrasColocadas = 0;
	    int maxPalabras = 10; 

	    for (String palabra : listaPalabras) {
	        palabra = palabra.toUpperCase();
	        boolean colocada = false;
	        int intentos = 0;

	        while (!colocada && intentos < 100) {
	            int[] direccion = DIRECCIONES[random.nextInt(DIRECCIONES.length)];
	            int fila = random.nextInt(tamanoTablero);
	            int col = random.nextInt(tamanoTablero);

	            if (colocarPalabra(tablero, palabra, fila, col, direccion)) {
	                colocada = true;
	                palabrasNivelActual.add(palabra);
	                palabrasColocadas++;
	            }

	            intentos++;
	        }

	        if (palabrasColocadas >= maxPalabras) break;
	    }

	    for (int fila = 0; fila < tamanoTablero; fila++) {
	        for (int col = 0; col < tamanoTablero; col++) {
	            if (tablero[fila][col] == '\0') {
	                tablero[fila][col] = (char) ('A' + random.nextInt(26));
	            }
	        }
	    }
	}
	
	private static void resetearSeleccion() {
	    for (JLabel lbl : etiquetasSeleccionadas) {
	        if (!etiquetasCorrectas.contains(lbl)) {
	            lbl.setBackground(Color.WHITE);
	        }
	    }
	    etiquetasSeleccionadas.clear();
	    puntosSeleccionados.clear();
	}
	
	
	private static void calcularAreaSeleccion() {
	    int x = Math.min(puntoInicioSeleccion.x, puntoFinSeleccion.x);
	    int y = Math.min(puntoInicioSeleccion.y, puntoFinSeleccion.y);
	    int ancho = Math.abs(puntoInicioSeleccion.x - puntoFinSeleccion.x);
	    int alto = Math.abs(puntoInicioSeleccion.y - puntoFinSeleccion.y);
	    areaSeleccion.setBounds(x, y, ancho, alto);
	}
	
	
	private static void seleccionarLetrasEnLinea(JPanel panelTablero) {
	    resetearSeleccion();

	    Point start = puntoInicioSeleccion;
	    Point end = puntoFinSeleccion;
	    
	    if (start == null || end == null) return;

	    Component startComp = panelTablero.getComponentAt(start);
	    Component endComp = panelTablero.getComponentAt(end);

	    if (!(startComp instanceof JLabel) || !(endComp instanceof JLabel)) return;

	    int filaInicio = (int) ((JLabel) startComp).getClientProperty("fila");
	    int colInicio = (int) ((JLabel) startComp).getClientProperty("col");
	    int filaFin = (int) ((JLabel) endComp).getClientProperty("fila");
	    int colFin = (int) ((JLabel) endComp).getClientProperty("col");

	    int dx = Integer.compare(filaFin, filaInicio);
	    int dy = Integer.compare(colFin, colInicio);

	    int fila = filaInicio;
	    int col = colInicio;
	    
	    if (filaInicio == filaFin && colInicio == colFin) return;

	    do {
	        if (!dentroDelTablero(fila, col)) {
	            break;
	        }

	        for (Component comp : panelTablero.getComponents()) {
	            if (comp instanceof JLabel) {
	                JLabel lbl = (JLabel) comp;
	                int f = (int) lbl.getClientProperty("fila");
	                int c = (int) lbl.getClientProperty("col");

	                if (f == fila && c == col) {
	                    etiquetasSeleccionadas.add(lbl);
	                    puntosSeleccionados.add(new Point(f, c));
	                    lbl.setBackground(Color.YELLOW);
	                    break;
	                }
	            }
	        }

	        if (fila == filaFin && col == colFin) {
	            break;
	        }

	        fila += dx;
	        col += dy;

	    } while (true);
	}
	
	private static void verificarPalabraSeleccionada() {
	    if (etiquetasSeleccionadas.isEmpty()) return;

	    StringBuilder palabraSeleccionada = new StringBuilder();
	    for (JLabel lbl : etiquetasSeleccionadas) {
	        palabraSeleccionada.append(lbl.getText());
	    }

	    String palabra = palabraSeleccionada.toString();

	    if (palabrasNivelActual.contains(palabra)) {
	    	
	    	efectoDeSonido("sounds/acierto.wav");

	        for (JLabel lbl : etiquetasSeleccionadas) {
	            lbl.setBackground(new Color(144, 238, 144));
	            etiquetasCorrectas.add(lbl);
	        }

	        for (JLabel lblPalabra : etiquetasPalabras) {
	            if (lblPalabra.getText().toUpperCase().contains(palabra)) {
	                lblPalabra.setForeground(Color.GRAY);
	                lblPalabra.setFont(lblPalabra.getFont().deriveFont(Font.ITALIC));
	            }
	        }

	        puntuacion += 10;
	        lblPuntaje.setText("Puntaje: " + puntuacion);
	        palabrasNivelActual.remove(palabra);
	        
	        if (palabrasNivelActual.isEmpty()) {
	            int opcion = JOptionPane.showConfirmDialog(frame,"Ha encontrado las 10 palabras.\n¿Deseas seguir jugando?","Nivel completado",JOptionPane.YES_NO_OPTION);

	            if (opcion == JOptionPane.YES_OPTION) {

	                iniciarSiguienteNivel();
	            } else {
	            	
	                guardarDatosJugador(nombreJugadorActual);
	                mostrarMenu();
	            }
	        }
	        
	        
	    } else {
	    	
	    	efectoDeSonido("sounds/error.wav");
	    	
	        for (JLabel lbl : etiquetasSeleccionadas) {
	            lbl.setBackground(new Color(255, 102, 102));
	        }

	        corazones--;
	        actualizarCorazones();

	        if (corazones <= 0) {
	        	cronometro.stop();
	            JOptionPane.showMessageDialog(frame, "Has perdido todas las vidas", "Juego terminado", JOptionPane.INFORMATION_MESSAGE);
	            guardarDatosJugador(nombreJugadorActual);
	            mostrarMenu();
	            return;
	        }
	    }

	    Timer timer = new Timer(500, e -> {
	        resetearSeleccion();
	    });
	    timer.setRepeats(false);
	    timer.start();
	}
	
	private static void iniciarSiguienteNivel() {
	    etiquetasCorrectas.clear();
	    etiquetasSeleccionadas.clear();
	    puntosSeleccionados.clear();

	    panelJuego(nombreJugadorActual);
	}
	
	private static void actualizarCorazones() {
	    panelCorazones.removeAll(); // Limpia antes de dibujar
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
	    panelCorazones.revalidate();
	    panelCorazones.repaint();
	}
	
	private static boolean dentroDelTablero(int fila, int col) {
	    return fila >= 0 && fila < tamanoTablero && col >= 0 && col < tamanoTablero;
	}
	
	private static void efectoDeSonido(String rutaSonido) {
	    if (!sonidoActivado) return;

	    try {
	        if (clipSonido != null && clipSonido.isRunning()) {
	            clipSonido.stop();
	            clipSonido.close();
	        }
	        AudioInputStream audioIn = AudioSystem.getAudioInputStream(new File(rutaSonido));
	        clipSonido = AudioSystem.getClip();
	        clipSonido.open(audioIn);
	        clipSonido.start();
	    } catch (Exception e) {
	        e.printStackTrace();
	    }
	}
	
}
