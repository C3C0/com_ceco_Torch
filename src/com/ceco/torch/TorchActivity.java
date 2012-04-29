package com.ceco.torch;

import java.io.IOException;
import java.util.List;

import com.ceco.torch.R;

import android.app.Activity;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.hardware.Camera;
import android.hardware.Camera.AutoFocusCallback;
import android.media.AudioManager;
import android.os.Bundle;
import android.view.Surface;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.Toast;
import android.widget.ToggleButton;

public class TorchActivity extends Activity implements OnClickListener//, SurfaceHolder.Callback
{
	private Camera camera;
	private Button btnCheck; 
	private ToggleButton btnToggle;
//	private SurfaceView surfaceView;    
//	private SurfaceHolder surfaceHolder;  
	private AudioManager audio;
	private Camera.PictureCallback pictureCallback;
	
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);        
        
        setContentView(R.layout.main);
        
        btnCheck = (Button)findViewById(R.id.btnCheck);
        btnCheck.setOnClickListener(this);      
        
        btnToggle = (ToggleButton)findViewById(R.id.btnToggle);
        btnToggle.setOnClickListener(this);

        audio = (AudioManager)this.getSystemService("audio");
        audio.setStreamMute(1, true);
        
        /*
        surfaceView = (SurfaceView) this.findViewById(R.id.surfaceView);      
        surfaceHolder = surfaceView.getHolder();      
        surfaceHolder.addCallback(this);      
        surfaceHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS); 
        */
        
        btnCheck.setEnabled(initCamera());
                
        this.pictureCallback = new Camera.PictureCallback() {
			
			@Override
			public void onPictureTaken(byte[] data, Camera camera) {
				// TODO Auto-generated method stub
				//camera.stopPreview();
				//switchOn();
				camera.takePicture(null, null, this);
			}
		};
    }
    
    private boolean initCamera()
    {
    	try
    	{
      		camera = Camera.open();   
			
        	Toast.makeText(this, "Kamera otvorena", Toast.LENGTH_LONG).show();        	        
        	
        	return true;
    	}
    	catch(Exception ex)
    	{
    		String msg = "Chyba inicializacie kamery: " + ex.getMessage();
    		Toast.makeText(this, msg, Toast.LENGTH_LONG).show();
    		return false;
    	}
    }

    private boolean supportsTorch()
    {		
    	try
    	{
			Camera.Parameters parameters = camera.getParameters();
	    	List<String> flashModes = parameters.getSupportedFlashModes();
	    	
	    	if(flashModes != null)
	    	{
	    		for(int i = 0; i < flashModes.size(); i++)
	    		{
	    			 Toast.makeText(this, flashModes.get(i), Toast.LENGTH_SHORT).show();            		 
	    		}
	    		
	        	if(flashModes.contains(Camera.Parameters.FLASH_MODE_TORCH))
	        	{
	        		Toast.makeText(this, "Kamera podporuje rezim Torch!!!", Toast.LENGTH_LONG).show();
	        		return true;        		
	        	}
	        	else
	        	{
	        		Toast.makeText(this, "Kamera nepodporuje rezim Torch", Toast.LENGTH_LONG).show();
	        		return false;
	        	}        		
	    	}
	    	else
	    	{
	    		Toast.makeText(this, "Nezname mody blesku", Toast.LENGTH_LONG).show();
	    		return false;
	    	}		
    	} 
    	catch(Exception ex)
    	{
    		String msg = "Chyba pri zistovani porpory Torch: " + ex.getMessage();
    		Toast.makeText(this, msg, Toast.LENGTH_LONG).show();
    		return false;
    	}    	    	
    }
    
	public void onClick(View v) 
	{
		if(v == btnCheck)
		{
			btnToggle.setEnabled(supportsTorch());			
		}
		
		if(v == btnToggle)
		{
			if(btnToggle.isChecked())
				switchOn();
			else
				switchOff();
		}
	}

	private void switchOn()
	{
		try
		{						
			Camera.Parameters params = camera.getParameters();
			params.setFlashMode(Camera.Parameters.FLASH_MODE_ON);
			params.setFocusMode(Camera.Parameters.FOCUS_MODE_CONTINUOUS_VIDEO);
			camera.setParameters(params);
			camera.startPreview();
			camera.autoFocus(new Camera.AutoFocusCallback() {
				
				@Override
				public void onAutoFocus(boolean success, Camera camera) {
					// TODO Auto-generated method stub
					camera.takePicture(null, null, null);
					//camera.autoFocus(this);
				}
			});
			//camera.takePicture(null, null, this.pictureCallback);
			//camera.stopPreview();
			
			//Toast.makeText(this, "Torch ON", Toast.LENGTH_LONG).show();
		}
		catch(Exception ex)
		{
			String msg = "Chyba pri zapnuti Torch: " + ex.getMessage();
			Toast.makeText(this, msg, Toast.LENGTH_LONG).show();
		}
	}
	
	private void switchOff()
	{
		try
		{
			camera.stopPreview();
			Camera.Parameters params = camera.getParameters();
			params.setFlashMode(Camera.Parameters.FLASH_MODE_OFF);
			camera.setParameters(params);
			camera.release();
			
			Toast.makeText(this, "Torch OFF", Toast.LENGTH_LONG).show();
		}
		catch(Exception ex)
		{
			String msg = "Chyba pri zapnuti Torch: " + ex.getMessage();
			Toast.makeText(this, msg, Toast.LENGTH_LONG).show();
		}	
	}
			
	
    @Override
    public void onDestroy()
    {
    	Toast.makeText(this, "onDestroy", Toast.LENGTH_SHORT).show();
    	if(camera != null)
    	{
    		if(btnToggle.isChecked())
			{
				switchOff();
				btnToggle.setChecked(false);
			}
    		
    		camera.release();
    		camera = null;    		
    	}
    	super.onDestroy();
    }

    /*
	@Override
	public void surfaceChanged(SurfaceHolder holder, int format, int width,
			int height) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void surfaceCreated(SurfaceHolder holder) {
		// TODO Auto-generated method stub
		try 
		{        
			camera.setPreviewDisplay(holder);      
		} 
		catch (IOException e) 
		{        
			e.printStackTrace();      
		}  
	}

	@Override
	public void surfaceDestroyed(SurfaceHolder holder) {
		// TODO Auto-generated method stub
		
	}
*/
}