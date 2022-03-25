package a2;

import static com.jogamp.opengl.GL2ES2.GL_FRAGMENT_SHADER;
import static com.jogamp.opengl.GL2ES2.GL_VERTEX_SHADER;

import java.io.File;
import java.io.IOException;
import java.util.Scanner;
import java.util.Vector;

import com.jogamp.opengl.GL;
import com.jogamp.opengl.GL2ES2;
import com.jogamp.opengl.GL4;
import com.jogamp.opengl.GLContext;
import com.jogamp.opengl.glu.GLU;
import com.jogamp.opengl.util.texture.Texture;
import com.jogamp.opengl.util.texture.TextureIO;

public class Utils {

	public static int createShaderProgram(String vS, String fS) {
		int[] vertCompiled = new int[1];
		int[] fragCompiled = new int[1];
		int[] linked = new int[1];
		
		GL4 gl = (GL4)GLContext.getCurrentGL();
		
		String vshaderSource[] = readShaderSource(vS);	
		String fshaderSource[] = readShaderSource(fS);
		
		int vShader = gl.glCreateShader(GL_VERTEX_SHADER);
		gl.glShaderSource(vShader, vshaderSource.length, vshaderSource, null, 0);
		gl.glCompileShader(vShader);
		
		checkOpenGLError();
		gl.glGetShaderiv(vShader, GL2ES2.GL_COMPILE_STATUS, vertCompiled, 0);
		if(vertCompiled[0] != 1) {
			System.out.println("Vertex compilation failed");
			printShaderLog(vShader);
		}
		
		int fShader = gl.glCreateShader(GL_FRAGMENT_SHADER);
		gl.glShaderSource(fShader, fshaderSource.length, fshaderSource, null, 0);
		gl.glCompileShader(fShader);
		
		checkOpenGLError();
		gl.glGetShaderiv(fShader, GL2ES2.GL_COMPILE_STATUS, fragCompiled, 0);
		if(fragCompiled[0] != 1) {
			System.out.println("Fragment compilation failed");
			printShaderLog(fShader);
		}
		
		if(vertCompiled[0] != 1 || fragCompiled[0] != 1) {
			System.out.println("\nCompilation error; return-flags: ");
			System.out.println(" vertCompiled = " + vertCompiled[0] + " ; fragCompiled = " + fragCompiled[0]);
		}
		
		int vfProgram = gl.glCreateProgram();
		gl.glAttachShader(vfProgram, vShader);
		gl.glAttachShader(vfProgram, fShader);
		gl.glLinkProgram(vfProgram);
		
		checkOpenGLError();
		gl.glGetProgramiv(vfProgram, GL2ES2.GL_LINK_STATUS, linked, 0);
		if(linked[0] != 1) {
			System.out.println("Linking failed");
			printProgramLog(vfProgram);
		}
		
		gl.glDeleteShader(vShader);
		gl.glDeleteShader(fShader);
		return vfProgram;
	}
	
	private static String[] readShaderSource(String filename) {
		Vector<String> lines = new Vector<String>();
		Scanner sc;
		String[] program;
		try {
			sc = new Scanner(new File(filename));
			while(sc.hasNext()) {
				lines.addElement(sc.nextLine());
			}
			program = new String[lines.size()];
			for(int i = 0; i < lines.size(); i++) {
				program[i] = (String)lines.elementAt(i) + "\n";
			}
		}catch(IOException e){
			System.err.println("IOException reading file: " + filename);
			return null;
		}
		return program;
	}
	
	private static void printShaderLog(int shader) {
		GL4 gl = (GL4)GLContext.getCurrentGL();
		int[] len = new int[1];
		int[] chWrittn = new int[1];
		byte[] log = null;
		
		gl.glGetShaderiv(shader, GL2ES2.GL_INFO_LOG_LENGTH, len, 0);
		if(len[0] > 0) {
			log = new byte[len[0]];
			gl.glGetShaderInfoLog(shader, len[0], chWrittn, 0, log, 0);
			System.out.println("Shader info log: ");
			for(int i = 0; i < log.length; i++) {
				System.out.println((char)log[i]);
			}
		}
	}
	
	public static void printProgramLog(int prog) {
		GL4 gl = (GL4)GLContext.getCurrentGL();
		int[] len = new int[1];
		int[] chWrittn = new int[1];
		byte[] log = null;
		
		gl.glGetProgramiv(prog, gl.GL_INFO_LOG_LENGTH, len, 0);
		if(len[0] > 0) {
			log = new byte[len[0]];
			gl.glGetShaderInfoLog(prog, len[0], chWrittn, 0, log, 0);
			System.out.println("Program info log: ");
			for(int i = 0; i < log.length; i++) {
				System.out.println((char)log[i]);
			}
		}
	}
	
	public static boolean checkOpenGLError() {
		GL4 gl = (GL4)GLContext.getCurrentGL();
		boolean foundError = false;
		GLU glu = new GLU();
		int glErr = gl.glGetError();
		while(glErr != GL.GL_NO_ERROR) {
			System.err.println("glErr: " + glu.gluErrorString(glErr));
			foundError = true;
			glErr = gl.glGetError();
		}
		return foundError;
	}

	public static int loadTexture(String textureFileName) {
		Texture tex = null;
		try {
			tex = TextureIO.newTexture(new File(textureFileName), false);
		}catch(Exception e) {
			e.printStackTrace();
		}
		int textureID = tex.getTextureObject();
		return textureID;
	}
	
}
