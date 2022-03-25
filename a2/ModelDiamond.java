package a2;

public class ModelDiamond {
	private float[] positions= {
		-.75f, 0.0f, .75f, .75f, 0.0f, .75f, 0.0f, 1.0f, 0.0f,
		.75f, 0.0f, .75f, .75f, 0.0f, -.75f, 0.0f, 1.0f, 0.0f,
		.75f, 0.0f, -.75f, -.75f, 0.0f, -.75f, 0.0f, 1.0f, 0.0f,
		-.75f, 0.0f, -.75f, -.75f, 0.0f, .75f, 0.0f, 1.0f, 0.0f,
		-.75f, 0.0f, .75f, .75f, 0.0f, .75f, 0.0f, -1.0f, 0.0f,
		.75f, 0.0f, .75f, .75f, 0.0f, -.75f, 0.0f, -1.0f, 0.0f,
		.75f, 0.0f, -.75f, -.75f, 0.0f, -.75f, 0.0f, -1.0f, 0.0f,
		-.75f, 0.0f, -.75f, -.75f, 0.0f, .75f, 0.0f, -1.0f, 0.0f,
	};
	
	private float[] textureCoordinates= {
		0.0f, 0.0f, 1.0f, 0.0f, 0.5f, 1.0f,		0.0f, 0.0f, 1.0f, 0.0f, 0.5f, 1.0f,
		0.0f, 0.0f, 1.0f, 0.0f, 0.5f, 1.0f,		0.0f, 0.0f, 1.0f, 0.0f, 0.5f, 1.0f,
		0.0f, 0.0f, 1.0f, 0.0f, 0.5f, 1.0f,		0.0f, 0.0f, 1.0f, 0.0f, 0.5f, 1.0f,
		0.0f, 0.0f, 1.0f, 0.0f, 0.5f, 1.0f,		0.0f, 0.0f, 1.0f, 0.0f, 0.5f, 1.0f,
	};
	
	private float locX, locY, locZ;
	
	public ModelDiamond(float locX, float locY, float locZ) {
		 this.locX = locX;
		 this.locY = locY;
		 this.locZ = locZ;
	}

	public float[] getPositions() {
		return positions;
	}
	
	public float[] getTextureCoordinates() {
		return textureCoordinates;
	}

	public float getLocX() {
		return locX;
	}

	public float getLocY() {
		return locY;
	}

	public float getLocZ() {
		return locZ;
	}

}
