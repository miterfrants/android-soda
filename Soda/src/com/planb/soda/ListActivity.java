package com.planb.soda;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import android.annotation.SuppressLint;
import android.app.ActionBar;
import android.content.Intent;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.StrictMode;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.ScaleAnimation;
import android.view.animation.TranslateAnimation;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.RelativeLayout.LayoutParams;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMap.OnMapClickListener;
import com.google.android.gms.maps.GoogleMap.OnMarkerClickListener;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.jeremyfeinstein.slidingmenu.lib.SlidingMenu;
import com.jeremyfeinstein.slidingmenu.lib.SlidingMenu.OnClosedListener;
import com.jeremyfeinstein.slidingmenu.lib.SlidingMenu.OnOpenedListener;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

@SuppressLint("InlinedApi")
public class ListActivity extends FragmentActivity {
	public String token="";
	public boolean isShowingGetMore=false;
	public LoadingLayout ldLayout=null;
	public String title="";
	public SlidingMenu slideMenu=null;
	public boolean isOpenedMap=false;
	public String bg ="";
	private int screenW=0;
	private Button btnGetMore;
	private String urlGet;
	private JSONArray arrRes;
	private String keyword;
	private String type;
	private String otherSource;
	private RelativeLayout rlForContent=null;
	private ScrollViewForPlaceItem scForPI =null;
	private GoogleMap map=null;
	private Button _btnNext=null;
	private Button _btnPrevious =null;
	private Button _btnTakeMeThere =null;
    public LocationManager lm=null;
    public LocationListener lmListener=null;
    public Runnable addThread=null;
	public static List<PlaceItem> arrListResult=new ArrayList<PlaceItem>();
	@Override 
	protected void onStart(){
		super.onStart();
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		ShareVariable.arrMarker.clear();
		arrListResult.clear();
		ShareVariable.isListActivity=true;
		super.onCreate(savedInstanceState);
		
		ViewServer.get(this).addWindow(this); 
		StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
	    StrictMode.setThreadPolicy(policy);
		setContentView(com.planb.soda.R.layout.activity_list);
		rlForContent= new RelativeLayout(this);

		ldLayout=new LoadingLayout(this);
		ldLayout.setAlpha(1.0f);
		ldLayout.bringToFront();
		rlForContent.addView(ldLayout);
		Util.checkLocationServices(this);
		ShareVariable.currentLocation=Util.getLocation(this);
		
		if(ShareVariable.currentLocation==null){
			Toast toast = Toast.makeText(this, "�Х��T�w GPS �w��w�}�ҡC", Toast.LENGTH_SHORT);
    		toast.show();
			return;
		}else{
//			Toast toast = Toast.makeText(this, ShareVariable.currentLocation.getLatitude()+","+ShareVariable.currentLocation.getLongitude(), Toast.LENGTH_LONG);
//    		toast.show();
		}
		slideMenu =(SlidingMenu) this.findViewById(com.planb.soda.R.id.rl_for_activity_list);
		keyword=getIntent().getStringExtra("keyword");
		type=getIntent().getStringExtra("type");
		otherSource=getIntent().getStringExtra("otherSource");
		bg=getIntent().getStringExtra("bg");
		
		rlForContent.setLayoutParams(new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT,RelativeLayout.LayoutParams.MATCH_PARENT));
		urlGet="https://maps.googleapis.com/maps/api/place/nearbysearch/json?"
				+ "location={lat},{lng}&radius=500&keyword="+keyword+"&sensor=false&"
				+ "key="+ShareVariable.GOOGLE_KEY+"&rankBy=prominence&types="+ type+"&language=zh-TW";
		
		screenW=getWindowManager().getDefaultDisplay().getWidth();
		btnGetMore=new Button(this);
		btnGetMore.setBackgroundResource(com.planb.soda.R.drawable.circle_button);
		RelativeLayout.LayoutParams rlpForBtnGetMore= new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT,RelativeLayout.LayoutParams.WRAP_CONTENT);
		rlpForBtnGetMore.width=((int) (screenW*0.145*0.8));
		rlpForBtnGetMore.height=((int) (screenW*0.145*0.8));
		rlpForBtnGetMore.bottomMargin=-rlpForBtnGetMore.width/2;
		rlpForBtnGetMore.leftMargin=-rlpForBtnGetMore.width/2;
		rlpForBtnGetMore.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
		if(ShareVariable.screenW==1080){
			btnGetMore.setTextSize(9);
		}else if(ShareVariable.screenW==720 || ShareVariable.screenW==768){
			btnGetMore.setTextSize(9);	
		}else{
			btnGetMore.setTextSize((int) (screenW * 0.01688888));
		}
		btnGetMore.setTextColor(0xFFFFFFFF);
		btnGetMore.setText("��h");
        btnGetMore.setTextSize(14);
		btnGetMore.setSingleLine(true);
		btnGetMore.setLayoutParams(rlpForBtnGetMore);
		btnGetMore.setAlpha(0.0f);
		btnGetMore.setClickable(false);
		btnGetMore.setOnClickListener(new View.OnClickListener() {
		    public void onClick(View v) {
		    	btnGetMore.setAlpha(0.0f);
		    	btnGetMore.setClickable(false);
		    	LinearLayout rlList =(LinearLayout) findViewById(com.planb.soda.R.id.ll_list);
		    	rlList.removeAllViews();
		    	String ip = Util.getIPAddress(true);
		    	ListActivity la=(ListActivity)	v.getContext();
				String url="http://"+ShareVariable.domain+ShareVariable.reportController+"?action=add-get-more&cate="+la.title+"&creator_ip="+ip;
				AsyncHttpClient client = new AsyncHttpClient();
		 		client.get(url, new AsyncHttpResponseHandler() {
				    @Override
				    public void onSuccess(String response) {
				    }
				    @Override
				    public void onFailure(Throwable e, String response){
				    }
				});
		 		getData(false);
		    }
		});
		rlForContent.addView(btnGetMore);
		//hideButtonGetMore(0);

		//list containerscForPI
		this.scForPI =(ScrollViewForPlaceItem) LayoutInflater.from(this).inflate(com.planb.soda.R.layout.scroll_view_for_place_item,null);
		rlForContent.addView(scForPI);
		scForPI.setLayoutParams(new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT,RelativeLayout.LayoutParams.MATCH_PARENT));
		scForPI.setPersistentDrawingCache(ViewGroup.PERSISTENT_SCROLLING_CACHE);
		scForPI.setAlwaysDrawnWithCacheEnabled(true);
		scForPI.setAlpha(0.0f);
		slideMenu.setContent(rlForContent);
		RelativeLayout rightView = (RelativeLayout)  LayoutInflater.from(this).inflate(com.planb.soda.R.layout.right_map,null);
		
		//map
		RelativeLayout.LayoutParams rlForMapPreviousButton =new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT,RelativeLayout.LayoutParams.WRAP_CONTENT);
		rlForMapPreviousButton.height=(int) (screenW*0.15);
		rlForMapPreviousButton.width= (int) (screenW*0.15);
		rlForMapPreviousButton.topMargin=(int) (screenW*0.0625);
		rlForMapPreviousButton.leftMargin=(int) (screenW*0.0625);
		_btnPrevious =new Button(this);
		_btnPrevious.setLayoutParams(rlForMapPreviousButton);
		_btnPrevious.setBackgroundResource(R.drawable.pre_btn);
		_btnPrevious.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                selectPreviousMarker();
            }
        });
		
		RelativeLayout.LayoutParams rlForMapNextButton = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT,RelativeLayout.LayoutParams.WRAP_CONTENT);
		_btnNext=new Button(this);
		rlForMapNextButton =new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT,RelativeLayout.LayoutParams.WRAP_CONTENT);
		rlForMapNextButton.height=(int) (screenW*0.15);
		rlForMapNextButton.width= (int) (screenW*0.15);
		rlForMapNextButton.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
		rlForMapNextButton.topMargin=(int) (screenW*0.0625);
		rlForMapNextButton.rightMargin=(int) (screenW*0.0625);
		_btnNext.setLayoutParams(rlForMapNextButton);
		_btnNext.setBackgroundResource(R.drawable.next_btn);
		_btnNext.setOnClickListener(new View.OnClickListener() {
		    public void onClick(View v) {
		    	selectNextMarker();
		    }
		});
		
		_btnTakeMeThere=new Button(this);
		RelativeLayout.LayoutParams rlForMapTakeMeThereButton =new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT,RelativeLayout.LayoutParams.WRAP_CONTENT);
		rlForMapTakeMeThereButton.width=(int) (screenW*0.50893);
		rlForMapTakeMeThereButton.height=(int) (screenW*0.15);
		rlForMapTakeMeThereButton.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
		rlForMapTakeMeThereButton.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
		rlForMapTakeMeThereButton.addRule(RelativeLayout.ALIGN_LEFT);
		rlForMapTakeMeThereButton.bottomMargin=(int) (screenW*0.0625);
		rlForMapTakeMeThereButton.rightMargin=(int) (screenW*0.0625);
		_btnTakeMeThere.setLayoutParams(rlForMapTakeMeThereButton);
		_btnTakeMeThere.setBackgroundResource(R.drawable.nav_btn);
		_btnTakeMeThere.setText("�ɯ�");
		_btnTakeMeThere.setTextSize(36);

		if(ShareVariable.screenW==1080){
			_btnTakeMeThere.setTextSize(20);
		}else if(ShareVariable.screenW==720 || ShareVariable.screenW==768){
			_btnTakeMeThere.setTextSize(38);
		}
		
		_btnTakeMeThere.setTextColor(0xFFFFFFFF);
		_btnTakeMeThere.setPadding(0,0,(int) (screenW*0.339062*0.5),0);
		_btnTakeMeThere.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
            	try{
            		Marker marker= ShareVariable.arrMarker.get(ShareVariable.selectedMarkerIndex);
            		String daddr=marker.getSnippet();
            		if(daddr.length()==0){
            			daddr=marker.getPosition().latitude +"," + marker.getPosition().longitude;
            		}
                	Intent navigation = new Intent(Intent.ACTION_VIEW, Uri
                	        .parse("http://maps.google.com/maps?saddr="
                	                + String.valueOf(ShareVariable.currentLocation.getLatitude())+ ","
                	                + String.valueOf(ShareVariable.currentLocation.getLongitude()) + "&daddr="
                	                + daddr));
//                	Log.d("test","test address:"+"http://maps.google.com/maps?saddr="
//                	                + String.valueOf(currentLocation.getLatitude())+ ","
//                	                + String.valueOf(currentLocation.getLongitude()) + "&daddr="
//                	                + daddr);
                	
                	navigation.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    navigation.setClassName("com.google.android.apps.maps", "com.google.android.maps.MapsActivity");
                    v.getContext().startActivity(navigation);	
            	}catch(Exception ex){
            		ex.printStackTrace();
            		Toast toast = Toast.makeText(v.getContext(), "�Цw��Google Map�A�ɯ�\����ϥΡC", Toast.LENGTH_SHORT);
            		toast.show();
            	}
            }
        });
		rightView.addView(_btnPrevious);
		rightView.addView(_btnNext);
		rightView.addView(_btnTakeMeThere);
		
		//map view
		android.support.v4.app.FragmentManager myFM = this.getSupportFragmentManager();
		final SupportMapFragment myMAPF = (SupportMapFragment) myFM
		                .findFragmentById(R.id.map);
		map=myMAPF.getMap();
        if(map!=null){
			myMAPF.getMap().getUiSettings().setZoomControlsEnabled(false);
			setMapCenter(ShareVariable.currentLocation.getLatitude(),ShareVariable.currentLocation.getLongitude(),15);
		    map.setOnMarkerClickListener(getMarkerClickListener());
		    map.setOnMapClickListener(getMapClickListener());
		}else{
			Toast toast = Toast.makeText(this, "�L�k�ϥαz��Google Map�A�·бz��s�C", Toast.LENGTH_SHORT);
    		toast.show();
		}
	    RelativeLayout.LayoutParams rlpForRightView = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT,RelativeLayout.LayoutParams.WRAP_CONTENT);
	    rlpForRightView.width=(int) (ShareVariable.screenW*0.9);
	    rlpForRightView.height=ShareVariable.screenH;
	    rightView.setLayoutParams(rlpForRightView);
	    
	    slideMenu.setMenu(rightView);
	    slideMenu.setMode(SlidingMenu.RIGHT);
	    slideMenu.setTouchModeAbove(SlidingMenu.TOUCHMODE_MARGIN);
	    slideMenu.setBehindWidth((int) (ShareVariable.screenW*0.8));
	    slideMenu.setOnOpenedListener(new OnOpenedListener(){
			@Override
			public void onOpened() {
				isOpenedMap=true;
				String ip = Util.getIPAddress(true);
				String url="http://"+ShareVariable.domain+ShareVariable.reportController+"?action=add-slide-to-map&cate="+title+"&creator_ip="+ip;
				AsyncHttpClient client = new AsyncHttpClient();
		 		client.get(url, new AsyncHttpResponseHandler() {
				    @Override
				    public void onSuccess(String response) {
				    }
				    @Override
				    public void onFailure(Throwable e, String response){
				    }
				});
			}

	    });
	    slideMenu.setOnClosedListener(new OnClosedListener(){
			@Override
			public void onClosed() {
				isOpenedMap=false;
			}	    
	    });
	    
		ActionBar bar=getActionBar();
		bar.setDisplayHomeAsUpEnabled(true);
		title=getIntent().getStringExtra("title");
		bar.setTitle("Soda | "+getIntent().getStringExtra("title"));
		
		getData(true);
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) 
	{    
		
	   switch (item.getItemId()) 
	   {        
	      case android.R.id.home:
	    	  if(isOpenedMap){
	    		  slideMenu.showContent();
	    		  return true;  
	    	  }
	         Intent intent = new Intent(this, MainActivity.class);            
	         intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP); 
	         startActivity(intent);            
	         return true;
	      default:
	         return super.onOptionsItemSelected(item);    
	   }
	}
	//if map was expanded then click back button return list view
    @Override
    public boolean onKeyUp(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
        	if(isOpenedMap){
	    		slideMenu.showContent();
	    		return true;  
	    	}
            startActivity(new  Intent(this, MainActivity.class));
            finish();
            return true;
        }
        return super.onKeyUp(keyCode, event);
    }
    public void hideContent(){
    	ldLayout.clearAnimation();
    	btnGetMore.clearAnimation();
    	scForPI.clearAnimation();
    	//�o��S��work
		ldLayout.show();
		
		scForPI.setAlpha(1.0f);
		AlphaAnimation aanimForScForPI= new AlphaAnimation(1.0f,0.0f);
		aanimForScForPI.setDuration(250);
		aanimForScForPI.setFillAfter(true);
		aanimForScForPI.setAnimationListener(new Animation.AnimationListener(){
		    @Override
		    public void onAnimationStart(Animation arg0) {
		    }           
		    @Override
		    public void onAnimationRepeat(Animation arg0) {
		    }      
		    @Override
		    public void onAnimationEnd(Animation arg0) {
		    	scForPI.setAlpha(0.0f);
		    	scForPI.setVisibility(View.INVISIBLE);
		    	scForPI.clearAnimation();
		    }
		});
		scForPI.setAnimation(aanimForScForPI);
		
		if(token.length()>0){
			btnGetMore.setVisibility(View.VISIBLE);
			btnGetMore.setAlpha(0.0f);
			AlphaAnimation aanimForBtnGetMore= new AlphaAnimation((float) 1,(float) 0);
			aanimForBtnGetMore.setDuration(2000);
			aanimForBtnGetMore.setFillAfter(true);
			btnGetMore.setAnimation(aanimForBtnGetMore);
			aanimForBtnGetMore.setAnimationListener(new Animation.AnimationListener(){
			    @Override
			    public void onAnimationStart(Animation arg0) {
			    }           
			    @Override
			    public void onAnimationRepeat(Animation arg0) {
			    }      
			    @Override
			    public void onAnimationEnd(Animation arg0) {
			    	btnGetMore.setAlpha(0);
			    	btnGetMore.setVisibility(View.INVISIBLE);
			    	btnGetMore.clearAnimation();
			    }
			});
		}
    }
    
    public void showContent(boolean isShowGetMore){
    	ldLayout.clearAnimation();
    	btnGetMore.clearAnimation();
    	scForPI.clearAnimation();
		ldLayout.hide();
		
		scForPI.setVisibility(View.VISIBLE);
		scForPI.setAlpha(1.0f);
		AlphaAnimation aanimForScForPI= new AlphaAnimation((float) 0.0f,(float) 1.0f);
		aanimForScForPI.setDuration(500);
		aanimForScForPI.setFillAfter(true);
		aanimForScForPI.setAnimationListener(new Animation.AnimationListener(){
		    @Override
		    public void onAnimationStart(Animation arg0) {
		    }           
		    @Override
		    public void onAnimationRepeat(Animation arg0) {
		    }      
		    @Override
		    public void onAnimationEnd(Animation arg0) {
		    	scForPI.setAlpha(1.0f);
		    	scForPI.setVisibility(View.VISIBLE);
		    	scForPI.clearAnimation();
		    }
		});
		scForPI.setAnimation(aanimForScForPI);
		
	    btnGetMore.bringToFront();
	    if(isShowGetMore){
		    btnGetMore.setVisibility(View.VISIBLE);
		    btnGetMore.setAlpha(1.0f);
			AlphaAnimation aanimForBtnGetMore= new AlphaAnimation(0.0f, 1.0f);
			aanimForBtnGetMore.setDuration(500);
			aanimForBtnGetMore.setFillAfter(true);
			btnGetMore.setAnimation(aanimForBtnGetMore);
			aanimForBtnGetMore.setAnimationListener(new Animation.AnimationListener(){
			    @Override
			    public void onAnimationStart(Animation arg0) {
			    }           
			    @Override
			    public void onAnimationRepeat(Animation arg0) {
			    }      
			    @Override
			    public void onAnimationEnd(Animation arg0) {
			    	btnGetMore.setAlpha(1.0f);
			    	btnGetMore.setVisibility(View.VISIBLE);
			    	btnGetMore.clearAnimation();
			    	btnGetMore.setClickable(true);
			    }
			});
	   }else{
		   btnGetMore.setAlpha(0.0f);
		   btnGetMore.setClickable(false);
	   }
    }
	public void getData(final boolean isNew){
		hideContent();
		Thread thread = new Thread()
		{
		    @Override
		    public void run() {
		        try {
		            getDataMain(isNew);
		        } catch (Exception e) {
		            e.printStackTrace();
		        }
		    }
		};
		thread.start();
	}
	
	public OnMarkerClickListener getMarkerClickListener()
	{
	    return new OnMarkerClickListener() 
	    {       
	        @Override
	        public boolean onMarkerClick(Marker marker) 
	        {
	        	marker.showInfoWindow();
	        	for(int i=0;i<ShareVariable.arrMarker.size();i++){
	        		if(ShareVariable.arrMarker.get(i).equals(marker)){
	        			ShareVariable.selectedMarkerIndex=i;
	        		}
	        	}
	        	//Log.d("test","test index:"+ShareVariable.selectedMarkerIndex);
	        	ShareVariable.isChangeMarkerIndex=false;
	        	((FrameLayout)scForPI.getChildAt(1)).getChildAt(0).scrollTo(0, (int) ((ShareVariable.screenW / 2 * ShareVariable.selectedMarkerIndex) - ShareVariable.screenW * 0.2));
	        	_btnTakeMeThere.setVisibility(View.VISIBLE);
	        	return false;
	        }
	    };      
	}
	
	public OnMapClickListener getMapClickListener()
	{
	    return new OnMapClickListener() 
	    {       
			@Override
			public void onMapClick(LatLng point) {
				ShareVariable.selectedMarkerIndex=-1;
				_btnTakeMeThere.setVisibility(View.INVISIBLE);
			}
	    };      
	}
	
	public void selectNextMarker(){
		if(ShareVariable.arrMarker.size()==0){
			return;
		}
		Log.d("test","test pre selectedMarkerIndex:"+ String.valueOf(ShareVariable.selectedMarkerIndex));
		if(ShareVariable.selectedMarkerIndex==-1){
			ShareVariable.selectedMarkerIndex=0;
		}else if(ShareVariable.selectedMarkerIndex==ShareVariable.arrMarker.size()-1){
			ShareVariable.selectedMarkerIndex=0;
		}else{
			ShareVariable.selectedMarkerIndex+=1;
		}
		Marker marker=ShareVariable.arrMarker.get(ShareVariable.selectedMarkerIndex);
		setMapCenter(marker.getPosition().latitude,marker.getPosition().longitude,15);
		ShareVariable.isChangeMarkerIndex=false;
		((FrameLayout)scForPI.getChildAt(1)).getChildAt(0).scrollTo(0, (int) ((screenW / 2 * ShareVariable.selectedMarkerIndex) - screenW * 0.2));
		_btnTakeMeThere.setVisibility(View.VISIBLE);
		marker.showInfoWindow();
	}
	
	public void selectPreviousMarker(){
		if(ShareVariable.arrMarker.size()==0){
			return;
		}
		if(ShareVariable.selectedMarkerIndex==-1){
			ShareVariable.selectedMarkerIndex=ShareVariable.arrMarker.size()-1;
		}else if(ShareVariable.selectedMarkerIndex==0){
			ShareVariable.selectedMarkerIndex=ShareVariable.arrMarker.size()-1;
		}else{
			ShareVariable.selectedMarkerIndex-=1;
		}
		Marker marker=ShareVariable.arrMarker.get(ShareVariable.selectedMarkerIndex);
		setMapCenter(marker.getPosition().latitude,marker.getPosition().longitude,15);
		marker.showInfoWindow();
		ShareVariable.isChangeMarkerIndex=false;
		((FrameLayout)scForPI.getChildAt(1)).getChildAt(0).scrollTo(0, (int) ((screenW / 2 * ShareVariable.selectedMarkerIndex) - screenW * 0.2));
		_btnTakeMeThere.setVisibility(View.VISIBLE);
	}
	public void showButtonGetMore(){
		btnGetMore.clearAnimation();
		if(this.token.length()==0){
			return;
		}
		ScaleAnimation sanim= new ScaleAnimation((float) 1,(float) 1.2,(float) 1,(float) 1.2);
		sanim.setDuration(260);
		sanim.setFillAfter(true);
		TranslateAnimation  tranAnim=new TranslateAnimation(0, btnGetMore.getHeight(),
				0,-btnGetMore.getHeight()
				);
		
		tranAnim.setDuration(260);
		tranAnim.setFillAfter(true);
		AnimationSet animSet=new AnimationSet(false);
		animSet.setFillAfter(true);
		animSet.addAnimation(sanim);
		animSet.addAnimation(tranAnim);
		isShowingGetMore=true;
		animSet.setAnimationListener(new Animation.AnimationListener(){
		    @Override
		    public void onAnimationStart(Animation arg0) {
		    	RelativeLayout.LayoutParams lp=(RelativeLayout.LayoutParams) btnGetMore.getLayoutParams();
		    }           
		    @Override
		    public void onAnimationRepeat(Animation arg0) {
		    }      

		    @Override
		    public void onAnimationEnd(Animation arg0) {
		    	RelativeLayout.LayoutParams lp;
		    	btnGetMore.clearAnimation();
		    	lp=(LayoutParams) btnGetMore.getLayoutParams();
                lp.width=(int) (screenW*0.145*0.8*1.2);
    			lp.height=(int) (screenW*0.145*0.8*1.2);
    			lp.bottomMargin=(int) (screenW*0.145*0.8/2);
    			lp.leftMargin=(int) (screenW*0.145*0.8/2);
		    	btnGetMore.setLayoutParams(lp);
        		final Handler handler = new Handler();
        	    handler.postDelayed(new Runnable() {
        	      @Override
        	      public void run() {
        	    	  hideButtonGetMore(260);
        	      }
        	    }, 3500);
		    }
		});
		btnGetMore.setAnimation(animSet);
		animSet.startNow();
	}
	public void hideButtonGetMore(int duration){
    	RelativeLayout.LayoutParams lp=(LayoutParams) btnGetMore.getLayoutParams();
    	lp.width=((int) (screenW*0.145*0.8));
    	lp.height=((int) (screenW*0.145*0.8));
    	lp.bottomMargin=-lp.width/2;
    	lp.leftMargin=-lp.width/2;
    	btnGetMore.setLayoutParams(lp);
		ScaleAnimation sanim= new ScaleAnimation((float) 1.2,(float) 1,(float) 1.2,(float) 1);
		sanim.setDuration(duration);
		sanim.setFillAfter(true);
		TranslateAnimation  tranAnim=new TranslateAnimation(btnGetMore.getHeight(),0,
					-btnGetMore.getHeight(),0
				);
		tranAnim.setDuration(duration);
		tranAnim.setFillAfter(true);
		AnimationSet animSet=new AnimationSet(false);
		animSet.setFillAfter(true);
		animSet.addAnimation(sanim);
		animSet.addAnimation(tranAnim);
		animSet.setAnimationListener(new Animation.AnimationListener(){
		    @Override
		    public void onAnimationStart(Animation arg0) {
		    }           
		    @Override
		    public void onAnimationRepeat(Animation arg0) {
		    }           
		    @Override
		    public void onAnimationEnd(Animation arg0) {
		    	isShowingGetMore=false;
		    	btnGetMore.clearAnimation();
		    	if(token.length()==0){
		    		btnGetMore.setAlpha(0);
		    		btnGetMore.setClickable(false);
		    		btnGetMore.setVisibility(View.INVISIBLE);
		    	}
		    }
		});
		btnGetMore.startAnimation(animSet);
	}
	public void getDataMain(boolean isNew){
		if(isNew){
			this.token="";
			this.arrListResult.clear();
		}
		if(ShareVariable.currentLocation ==  null){
			//peter modify pop up
			return;
		};
		String urlTempGet =urlGet.replace("{lat}",String.valueOf(ShareVariable.currentLocation.getLatitude())).replace("{lng}",String.valueOf( ShareVariable.currentLocation.getLongitude()));
		if(this.token.length()>0){
			urlTempGet+="&pagetoken="+this.token;
		}

		AsyncHttpClient client = new AsyncHttpClient();
 		client.get(urlTempGet, new AsyncHttpResponseHandler() {
		    @Override
		    public void onSuccess(String response) {
		    	try{
		    		JSONObject res=new JSONObject(response);
		    		if(otherSource.length()>0){
		    			String urlOtherSource="http://"+ ShareVariable.domain+otherSource+"&lat="+String.valueOf(ShareVariable.currentLocation.getLatitude())+"&lng="+String.valueOf(ShareVariable.currentLocation.getLongitude());
		    			JSONObject jsOtherSource=new JSONObject(Util.getRemoteString(urlOtherSource));
		    			JSONArray jsArrayOtherSource=jsOtherSource.getJSONArray("results");
		    			if(res.getString("status")=="OK" && res.getJSONArray("results").length()>0){
		    				for(int i=0;i<jsArrayOtherSource.length();i++){
		    					res.getJSONArray("results").put(jsArrayOtherSource.get(i));
		    				}
		    			}else{
		    				res=jsOtherSource;
		    			}
		    		}
		    		generateList(res);
		    	}catch(Exception ex){
		    		ex.printStackTrace();
		    	}
		    }
		    @Override
		    public void onFailure(Throwable e, String response){
		    	Log.d("test","test async get google api error:"+ e.getMessage());
		    }
		});
	}
	public void setMapCenter(double lat,double lng,int zoomLevel){
		CameraUpdate center=CameraUpdateFactory.newLatLng(new LatLng(lat,lng));
	    CameraUpdate zoom=CameraUpdateFactory.zoomTo(zoomLevel);
		map.moveCamera(zoom);
	    map.animateCamera(center,280,null);
	}
	public void generateList(final JSONObject res){
	   try{
		   //data prepare
		   String status =res.getString("status");
		   
		   if(status.equals("OK") && res.getJSONArray("results").length()>0){
			   arrRes=res.getJSONArray("results");
			   for(int i=0;i<arrRes.length();i++){
				   	JSONObject item= arrRes.getJSONObject(i);
				   	JSONObject location=item.getJSONObject("geometry").getJSONObject("location");
				   	PlaceItem btn=new PlaceItem(this.getApplicationContext(),this.getWindow().getWindowManager().getDefaultDisplay().getWidth());
					btn.bottomLayout.title.setText(item.getString("name"));
					btn.name=item.getString("name");
					btn.lat=Double.parseDouble(location.getString("lat"));
					btn.lng=Double.parseDouble(location.getString("lng"));
                    btn.rateLayout.setRating(0);
                    if(item.has("rating")){
						btn.rateLayout.setRating((float) item.getDouble("rating")/5);
						if(item.getDouble("rating")==0){
							btn.rateLayout.setVisibility(View.INVISIBLE);
						}
						btn.rateLayout.txtRate.setText(String.valueOf(item.getDouble("rating")));
                        btn.rateLayout.txtRate.setTextColor(0xFFFFFFFF);
                        btn.rateLayout.setBackgroundColor(0xCC999999);
					}
					if(item.has("photos")){
						String photoRef=item.getJSONArray("photos").getJSONObject(0).getString("photo_reference");
						String url ="https://maps.googleapis.com/maps/api/place/photo?maxwidth=400&photoreference="+photoRef+"&sensor=false&key="+ShareVariable.GOOGLE_KEY;
						btn.bg.setTag(url);
						new DownloadImagesTask().execute(btn.bg);
					}else{
						if(bg.length()>0){
							btn.bg.setBackgroundResource(getResources().getIdentifier(bg, "drawable", getPackageName()));
						}
					}
					arrListResult.add(btn);
					btn.buildDist();
					btn.setOnClickListener(new OnClickListener(){

						@Override
						public void onClick(View v) {
							ShareVariable.isChangeMarkerIndex=false;
							ShareVariable.selectedMarkerIndex=((PlaceItem) v).index;
							((FrameLayout)scForPI.getChildAt(1)).getChildAt(0).scrollTo(0, (int) ((screenW / 2 * ShareVariable.selectedMarkerIndex) - screenW * 0.2));
							Marker marker= ShareVariable.arrMarker.get(ShareVariable.selectedMarkerIndex);
							marker.showInfoWindow();
							setMapCenter(marker.getPosition().latitude,marker.getPosition().longitude,15);
							slideMenu.showMenu();
						}
						
					});
			   }

			   Collections.sort(arrListResult, new Comparator<PlaceItem>()  {
			        @Override
			        public int compare(PlaceItem s1, PlaceItem s2) {
			        	if(s1.dist<s2.dist){
			        		return -1;
			        		
			        	}else if(s1.dist>s2.dist){
			        		return 1;
			        	}else{
			        		return 0;
			        	}
			        }
			    });

			   //ui 
			   this.runOnUiThread(addThread =new Runnable(){
				   @Override
				   public void run(){
					   if(!ShareVariable.isListActivity){
						   return;
					   }
					   LinearLayout rlList =(LinearLayout) findViewById(com.planb.soda.R.id.ll_list);
					   
					   rlList.setBackgroundColor(0xFFcccccc);
					   rlList.removeAllViews();
					   for(int i=0;i<arrListResult.size();i++){
						    RelativeLayout.LayoutParams lpForButton= new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT,RelativeLayout.LayoutParams.WRAP_CONTENT);
							lpForButton.height=screenW/2;
							PlaceItem btn=(PlaceItem) arrListResult.get(i);
							btn.index=i;
							btn.setLayoutParams(lpForButton);
							LatLng locate=new LatLng(arrListResult.get(i).lat,arrListResult.get(i).lng);
							if(map != null){
								Marker marker =map.addMarker(new MarkerOptions()
																	.position(locate)
																	.title(arrListResult.get(i).name)
																	.snippet(arrListResult.get(i).address)
															);
								ShareVariable.arrMarker.add(marker);
							}
							try{
								rlList.addView(arrListResult.get(i));
							}catch(Exception ex){
								rlList.removeView(arrListResult.get(i));
								rlList.addView(arrListResult.get(i));
								
							}
							
					   }
					   if(res.has("next_page_token")){
						   try {
								token=res.getString("next_page_token");
							} catch (JSONException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
						   showContent(true);
					   }else{
						   token="";
						   showContent(false);
					   }
					   
					   if(ShareVariable.arrMarker.size()>0){
						   ShareVariable.arrMarker.get(0).showInfoWindow();
					   }
				   }
			   });
			   
		   }else{
			   
			   this.runOnUiThread(new Runnable(){
				   @Override
				   public void run(){
					   ldLayout.txtLoadingStatus.setText("�z�Ҧb����m�S�����");
					   ldLayout.imgLoading.setVisibility(View.INVISIBLE);
				   }
				});
			   
			  
			   
		   }
	   }catch(Exception ex){
		   Log.d("test","test:exception occur:" + ex.getMessage());
		   ex.printStackTrace();
	   }
	}
    public void onDestroy() {
        super.onDestroy();  
   }  
    public void onResume() {
        super.onResume();  
        if(Util.lmListener==null){
        	ShareVariable.currentLocation=Util.getLocation(this);
        }
   }
    public void onPause(){
    	ShareVariable.isListActivity=false;
    	Util.stopUpdateLocation();
        super.onPause();
    }
}
