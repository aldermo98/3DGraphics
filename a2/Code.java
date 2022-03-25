package a2;

import static com.jogamp.opengl.GL.GL_COLOR_BUFFER_BIT;
import static com.jogamp.opengl.GL.GL_DEPTH_BUFFER_BIT;
import static com.jogamp.opengl.GL.GL_TRIANGLES;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.nio.FloatBuffer;

import javax.swing.JFrame;

import org.joml.Matrix4f;
import org.joml.Vector3f;

import com.jogamp.common.nio.Buffers;
import com.jogamp.opengl.GL;
import com.jogamp.opengl.GL4;
import com.jogamp.opengl.GLAutoDrawable;
import com.jogamp.opengl.GLContext;
import com.jogamp.opengl.GLEventListener;
import com.jogamp.opengl.awt.GLCanvas;
import com.jogamp.opengl.util.Animator;

public class Code extends JFrame implements GLEventListener, KeyListener{
	private GLCanvas myCanvas;
	private int renderingProgram;
	private int vao[] = new int[1];
	private int vbo[] = new int[5];
	
	private float cameraX, cameraY, cameraZ,
	 			  camX, camY, camZ;	
	private ModelCube obj_cube;
	private ModelDiamond obj_diamond;
	private int brickTexture;
	private int abstractTexture;
	
	private ImportedModel chime;
	private int numObjVertices;
	
	private FloatBuffer vals = Buffers.newDirectFloatBuffer(16);
	private Matrix4f pMat = new Matrix4f(), 	//perspective matrix
					 vMat = new Matrix4f(), 	//view matrix
					 mMat = new Matrix4f(), 	//model matrix
					 mvMat = new Matrix4f();	//model-view matrix
	private int mvLoc, pLoc;
	private float aspect;
	
	private double startTime;
	private double elapsedTime;
	private double timeFactor;
		
	public Code() {		
		setTitle("Assignment 2");
		setSize(1000, 1000);
		setLocationRelativeTo(null);	//center the window....must be called after setSize()
		
		myCanvas = new GLCanvas();
		myCanvas.addGLEventListener(this);
		this.add(myCanvas);
		
		this.addKeyListener(this);

		Animator animtr = new Animator(myCanvas);
		animtr.start();		//repeatedly calls display() funct
		
		this.setFocusable(true);
		this.setVisible(true);
	}
	
	public void display(GLAutoDrawable drawable) {
		GL4 gl = (GL4)GLContext.getCurrentGL();
		gl.glClear(GL_DEPTH_BUFFER_BIT);
		gl.glClear(GL_COLOR_BUFFER_BIT);
		gl.glUseProgram(renderingProgram);
		
		elapsedTime = System.currentTimeMillis() - startTime;
		timeFactor = elapsedTime / 1000.0;
		
		//get references to uniform vars for mv and p matrices
		mvLoc = gl.glGetUniformLocation(renderingProgram, "mv_matrix");
		pLoc = gl.glGetUniformLocation(renderingProgram, "p_matrix");
		
		//build p matrix
		aspect = (float)myCanvas.getWidth() / (float)myCanvas.getHeight();
		pMat.setPerspective((float)Math.toRadians(60.0f), aspect, 0.1f, 1000.0f);
		
		//build v matrix
		vMat.translation(-cameraX, -cameraY, -cameraZ);
		vMat.rotateXYZ(-camX, -camY, -camZ);
		
		/******************************build m and mv matrices for cube***********************************/
		mMat.identity();
		mMat.translate((float)Math.sin(0.35f*timeFactor)*2.0f, (float)Math.sin(0.52f*timeFactor)*2.0f, (float)Math.sin(0.7f*timeFactor)*2.0f);
		mMat.rotateXYZ(1.75f*(float)timeFactor, 1.75f*(float)timeFactor, 1.75f*(float)timeFactor);
		
		mvMat.identity();
		mvMat.mul(vMat);
		mvMat.mul(mMat);
		
		//copy p and mv mat to corresponding uniform vars
		gl.glUniformMatrix4fv(mvLoc, 1, false, mvMat.get(vals));
		gl.glUniformMatrix4fv(pLoc, 1, false, pMat.get(vals));
		
		//associate vbo with corresponding vert attr in vert shader
		gl.glBindBuffer(GL.GL_ARRAY_BUFFER, vbo[0]);
		gl.glVertexAttribPointer(0, 3, GL.GL_FLOAT, false, 0, 0);
		gl.glEnableVertexAttribArray(0);
		
		gl.glBindBuffer(GL.GL_ARRAY_BUFFER, vbo[1]);
		gl.glVertexAttribPointer(1, 2, GL.GL_FLOAT, false, 0, 0);
		gl.glEnableVertexAttribArray(1);
		
		gl.glActiveTexture(GL.GL_TEXTURE0);
		gl.glBindTexture(GL.GL_TEXTURE_2D, abstractTexture);
		
		//adjust opengl settings and draw model
		gl.glEnable(GL.GL_DEPTH_TEST);
		gl.glDepthFunc(GL.GL_LEQUAL);
		gl.glDrawArrays(GL_TRIANGLES, 0, 36);
		
		
		/******************************build m and mv matrices for diamond***********************************/
		mMat.translation(obj_diamond.getLocX(), obj_diamond.getLocY(), obj_diamond.getLocZ());
		
		mvMat.identity();
		mvMat.mul(vMat);
		mvMat.mul(mMat);
		
		//copy p and mv mat to corresponding uniform vars
		gl.glUniformMatrix4fv(mvLoc, 1, false, mvMat.get(vals));
		gl.glUniformMatrix4fv(pLoc, 1, false, pMat.get(vals));
		
		//associate vbo with corresponding vert attr in vert shader
		gl.glBindBuffer(GL.GL_ARRAY_BUFFER, vbo[2]);
		gl.glVertexAttribPointer(0, 3, GL.GL_FLOAT, false, 0, 0);
		gl.glEnableVertexAttribArray(0);
		
		gl.glBindBuffer(GL.GL_ARRAY_BUFFER, vbo[3]);
		gl.glVertexAttribPointer(1, 2, GL.GL_FLOAT, false, 0, 0);
		gl.glEnableVertexAttribArray(1);
		
		gl.glActiveTexture(GL.GL_TEXTURE0);
		gl.glBindTexture(GL.GL_TEXTURE_2D, brickTexture);
		
		//adjust opengl settings and draw model
		gl.glEnable(GL.GL_DEPTH_TEST);
		gl.glDepthFunc(GL.GL_LEQUAL);
		gl.glDrawArrays(GL_TRIANGLES, 0, 24);
		
		
		/*************************************************************************/
		//associate vbo with corresponding vert attr in vert shader
		gl.glBindBuffer(GL.GL_ARRAY_BUFFER, vbo[4]);
		gl.glVertexAttribPointer(0, 3, GL.GL_FLOAT, false, 0, 0);
		gl.glEnableVertexAttribArray(0);
		
		//adjust opengl settings and draw model
		gl.glEnable(GL.GL_DEPTH_TEST);
		gl.glDepthFunc(GL.GL_LEQUAL);
		gl.glDrawArrays(GL_TRIANGLES, 0, chime.getNumVertices());
		
		this.requestFocus(true);
	}
	
	public void init(GLAutoDrawable drawable) {
		GL4 gl = (GL4)GLContext.getCurrentGL();
		renderingProgram = Utils.createShaderProgram("a2/vertShader.glsl", "a2/fragShader.glsl");
		
		cameraX = 0.0f; cameraY = 0.0f; cameraZ = 8.0f;
		camX = 0.0f; camY = 0.0f; camZ = 0.0f;
		
		obj_cube = new ModelCube(0.0f, -2.0f, 0.0f);
		abstractTexture = Utils.loadTexture("a2/dice.jpg");
		
		obj_diamond = new ModelDiamond(0.0f, 0.0f, 0.0f);
		brickTexture = Utils.loadTexture("a2/brick1.jpg");
		
		chime = new ImportedModel("a2/chime.obj");
		
		setupVertices();
		startTime = System.currentTimeMillis();
		
		System.out.println("OpenGL: " + gl.glGetString(GL.GL_VERSION));
		System.out.println("JOGL: " + Package.getPackage("com.jogamp.opengl").getImplementationVersion());
	}
	
	public void setupVertices() {
		GL4 gl = (GL4)GLContext.getCurrentGL();
		
		numObjVertices = chime.getNumVertices();
		Vector3f[] vertices = chime.getVertices();
		
		float[] pvalues = new float[numObjVertices*3];
		
		for(int i = 0; i < numObjVertices; i++) {
			pvalues[i*3] = (float)(vertices[i]).x();
			pvalues[i*3+1] = (float)(vertices[i]).y();
			pvalues[i*3+2] = (float)(vertices[i]).z();
		}
		
		gl.glGenVertexArrays(vao.length, vao, 0);
		gl.glBindVertexArray(vao[0]);
		gl.glGenBuffers(vbo.length, vbo, 0);
		
		gl.glBindBuffer(GL.GL_ARRAY_BUFFER, vbo[0]);
		FloatBuffer cubeBuffer = Buffers.newDirectFloatBuffer(obj_cube.getPositions());
		gl.glBufferData(GL.GL_ARRAY_BUFFER, cubeBuffer.limit()*4, cubeBuffer, GL.GL_STATIC_DRAW);
		
			gl.glBindBuffer(GL.GL_ARRAY_BUFFER, vbo[1]);
			FloatBuffer cTexBuffer = Buffers.newDirectFloatBuffer(obj_cube.getTextureCoordinates());
			gl.glBufferData(GL.GL_ARRAY_BUFFER, cTexBuffer.limit()*4, cTexBuffer, GL.GL_STATIC_DRAW);
		
		gl.glBindBuffer(GL.GL_ARRAY_BUFFER, vbo[2]);
		FloatBuffer diamondBuffer = Buffers.newDirectFloatBuffer(obj_diamond.getPositions());
		gl.glBufferData(GL.GL_ARRAY_BUFFER, diamondBuffer.limit()*4, diamondBuffer, GL.GL_STATIC_DRAW);
		
			gl.glBindBuffer(GL.GL_ARRAY_BUFFER, vbo[3]);
			FloatBuffer dTexBuffer = Buffers.newDirectFloatBuffer(obj_diamond.getTextureCoordinates());
			gl.glBufferData(GL.GL_ARRAY_BUFFER, dTexBuffer.limit()*4, dTexBuffer, GL.GL_STATIC_DRAW);
		
		
		
		gl.glBindBuffer(GL.GL_ARRAY_BUFFER, vbo[4]);
		FloatBuffer vertBuffer = Buffers.newDirectFloatBuffer(pvalues);
		gl.glBufferData(GL.GL_ARRAY_BUFFER, vertBuffer.limit()*4, vertBuffer, GL.GL_STATIC_DRAW);
	}
	
	public void reshape(GLAutoDrawable drawable, int x, int y, int width, int height) {}
	
	public void dispose(GLAutoDrawable drawable) {}
	
	public static void main(String[] args) {
		new Code();
	}

	@Override
	public void keyPressed(KeyEvent e) {
		switch(e.getKeyCode()) {
			case KeyEvent.VK_W: cameraZ = cameraZ - 0.1f;
				break;
			case KeyEvent.VK_S: cameraZ = cameraZ + 0.1f;
				break;
			case KeyEvent.VK_A: cameraX = cameraX - 0.1f;
				break;
			case KeyEvent.VK_D: cameraX = cameraX + 0.1f;
				break;
			case KeyEvent.VK_E: cameraY = cameraY + 0.1f;
				break;
			case KeyEvent.VK_Q: cameraY = cameraY - 0.1f;
				break;
			case KeyEvent.VK_UP: camX = camX - 0.1f;
				break;
			case KeyEvent.VK_DOWN: camX = camX + 0.1f;
				break;	
			case KeyEvent.VK_LEFT: camY = camY - 0.1f;
				break;
			case KeyEvent.VK_RIGHT: camY = camY + 0.1f;
				break;
			case KeyEvent.VK_SPACE: System.out.println("space");
				break;
			default: System.out.println("invalid key entered");
		}
		
	}
	@Override
	public void keyTyped(KeyEvent e) {}
	@Override
	public void keyReleased(KeyEvent e) {}
}
