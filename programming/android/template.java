import com.dynamsoft.dbr.BarcodeReader;
import com.dynamsoft.dbr.BarcodeReaderException;
import com.dynamsoft.dbr.DBRLTSLicenseVerificationListener;
import com.dynamsoft.dbr.DCESettingParameters;
import com.dynamsoft.dbr.TextResultCallback;
import com.dynamsoft.dbr.TextResult;
import com.dynamsoft.dce.CameraEnhancer;
import com.dynamsoft.dce.CameraLTSLicenseVerificationListener;
import com.dynamsoft.dce.CameraState;
import com.dynamsoft.dce.CameraView;

public class MainActivity extends AppCompatActivity {
   CameraView cameraView;            
   CameraEnhancer mCameraEnhancer;
   TextResultCallback mTextResultCallback;
   BarcodeReader reader;
   TextView tvRes;
      
   @Override
   protected void onCreate(Bundle savedInstanceState) {
      super.onCreate(savedInstanceState);
      setContentView(R.layout.activity_main);

      //Deploy the camera with Dynamsoft Camera Enhancer.
      cameraView = findViewById(R.id.cameraView);
      tvRes = findViewById(R.id.tv_res);
      mCameraEnhancer = new CameraEnhancer(MainActivity.this);
      mCameraEnhancer.addCameraView(cameraView);
      com.dynamsoft.dce.DMLTSConnectionParameters info = new com.dynamsoft.dce.DMLTSConnectionParameters();
      info.organizationID = "Put your organizationID here.";
      mCameraEnhancer.initLicenseFromLTS(info,new CameraLTSLicenseVerificationListener() {
         @Override
         public void LTSLicenseVerificationCallback(boolean isSuccess, Exception error) {
            if(!isSuccess){ error.printStackTrace(); }
         }
      });
      mCameraEnhancer.setCameraDesiredState(CameraState.CAMERA_STATE_ON);
      mCameraEnhancer.startScanning();

      //Initialize Dynamsoft Barcode Reader from License Tracking Server.
      try {
         reader = new BarcodeReader();
         com.dynamsoft.dbr.DMLTSConnectionParameters parameters = new com.dynamsoft.dbr.DMLTSConnectionParameters();
         parameters.organizationID = "Put your organizationID here.";
         reader.initLicenseFromLTS(parameters, new DBRLTSLicenseVerificationListener() {
            @Override
            public void LTSLicenseVerificationCallback(boolean b, Exception e) {
               if (!b) { e.printStackTrace(); }
            }
         });
      } catch (BarcodeReaderException e) {
         e.printStackTrace();
      }
      //Get and display the text result.
      mTextResultCallback = new TextResultCallback() {
         @Override
         public void textResultCallback(int i, TextResult[] textResults, Object o) {
            if (textResults != null && textResults.length > 0) {
               String strRes = "";
               for ( i = 0; i < textResults.length; i++)
                  strRes += textResults[i].barcodeText + "\n\n";
               tvRes.setText(strRes);
            }
         }
      };                
      //Set DCE setting parameters in Dynamsoft Barcode Reader.
      //The camera instance will be transferred as an argument to the barcode reader.
      //With the Camera instance, the barcode reader will automatically use decodeBuffer as the decode method.
      DCESettingParameters dceSettingParameters = new DCESettingParameters();
      dceSettingParameters.cameraInstance = mCameraEnhancer;
      dceSettingParameters.textResultCallback = mTextResultCallback;
      reader.SetCameraEnhancerParam(dceSettingParameters);
   }
      
   @Override
   public void onResume() {
      reader.StartCameraEnhancer();
      super.onResume();
   }

   @Override
   public void onPause() {
      reader.StopCameraEnhancer();
      super.onPause();
   }
}