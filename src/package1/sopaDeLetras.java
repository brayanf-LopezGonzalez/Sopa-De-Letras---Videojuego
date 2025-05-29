package package1;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.io.FileWriter;
import java.io.Reader;
import java.io.Writer;
import java.lang.reflect.Type;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;

public class sopaDeLetras{
	
	private static JFrame frame;
    private static JPanel panelPrincipal;

    public static void main(String[] args) {
        frame = new JFrame("Sopa de Letras");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(800, 600);
        frame.setLocationRelativeTo(null);

        mostrarMenu();
        frame.setVisible(true);
    }
    
    private static void mostrarMenu() {
        panelPrincipal = new JPanel(new BorderLayout());
        panelPrincipal.setBorder(BorderFactory.createEmptyBorder(50, 50, 50, 50));

        JLabel titulo = new JLabel("SOPA DE LETRAS", SwingConstants.CENTER);
        titulo.setFont(new Font("Arial", Font.BOLD, 48));
        panelPrincipal.add(titulo, BorderLayout.NORTH);

        JPanel panelBotones = new JPanel(new GridLayout(2, 1, 0, 30));
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
		JOptionPane.showMessageDialog(null, "Iniciando juego... Esta funcionalidad se implementará luego.");
	}

	public class GestorMarcadores {

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
	    panelBoton.add(btnVolver);
	    panelMarcadores.add(panelBoton, BorderLayout.SOUTH);

	    frame.getContentPane().removeAll();
	    frame.add(panelMarcadores);
	    frame.revalidate();
	    frame.repaint();
	}
	
	public class Jugador {
		
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
	
	 
}
