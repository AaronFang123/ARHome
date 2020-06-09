package com.ar.arhome.Activity;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.ar.arhome.R;
import com.google.ar.core.Anchor;
import com.google.ar.sceneform.AnchorNode;
import com.google.ar.sceneform.math.Vector3;
import com.google.ar.sceneform.rendering.ModelRenderable;
import com.google.ar.sceneform.rendering.ViewRenderable;
import com.google.ar.sceneform.ux.ArFragment;
import com.google.ar.sceneform.ux.TransformableNode;

public class arFrameActivity extends AppCompatActivity implements View.OnClickListener {

    ArFragment arFragment;
    private ModelRenderable bearRenderable;
    private ModelRenderable catRenderable;

    ImageView bear,cat;
    ImageView btn_back, btn_screenShot, btn_help;
    View[] arrayView;

    int selected = 1;//default selected

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ar_frame);
        arFragment = (ArFragment)getSupportFragmentManager().findFragmentById(R.id.sceneform_fragment);
        // View
        bear = (ImageView) findViewById(R.id.bear);
        cat = (ImageView)findViewById(R.id.cat);

        initFunctionBar();
        setArrayView();
        setClickListener();
        setupModel();

        arFragment.setOnTapArPlaneListener((hitResult, plane, motionEvent) -> {
            // when  user tap on plane,add model
            Anchor anchor = hitResult.createAnchor();
            AnchorNode anchorNode = new AnchorNode(anchor);
            anchorNode.setParent(arFragment.getArSceneView().getScene());
            createModel(anchorNode, selected);
        });
    }

    private void initFunctionBar() {
        btn_back = (ImageView)findViewById(R.id.ar_back);
        btn_screenShot = (ImageView)findViewById(R.id.ar_screenshot);
        btn_help = (ImageView)findViewById(R.id.ar_help);

        // 返回
        btn_back.setOnClickListener((v)->{
            finish();
        });
        // 截屏 开发中
        btn_screenShot.setOnClickListener((v)->{
            Toast.makeText(this,"开发中", Toast.LENGTH_SHORT).show();
        });
        // 帮助页面
        btn_help.setOnClickListener(v -> {
            Intent intent = new Intent(this, helpActivity.class);
            startActivity(intent);
        });
    }

    private void setupModel() {
        ModelRenderable.builder()
                .setSource(this, R.raw.bear)
                .build().thenAccept(renderable -> bearRenderable = renderable)
                .exceptionally(
                        throwable -> {
                            Toast.makeText(this, "模型加载失败", Toast.LENGTH_SHORT).show();
                            return null;
                        });
        ModelRenderable.builder()
                .setSource(this, R.raw.cat)
                .build().thenAccept(renderable -> catRenderable = renderable)
                .exceptionally(
                        throwable -> {
                            Toast.makeText(this, "模型加载失败", Toast.LENGTH_SHORT).show();
                            return null;
                        });
    }

    private void createModel(AnchorNode anchorNode, int selected) {
        if(selected == 1) {
            TransformableNode bear = new TransformableNode(arFragment.getTransformationSystem());
            bear.setParent(anchorNode);
            bear.setRenderable(bearRenderable);
            bear.select();
            addnNameView(anchorNode, bear ,"BEAR");

        }

        else if(selected == 2){
            TransformableNode cat = new TransformableNode(arFragment.getTransformationSystem());
            cat.setParent(anchorNode);
            cat.setRenderable(catRenderable);
            cat.select();
            addnNameView(anchorNode, cat ,"CAT");
        }
    }

    private void addnNameView(AnchorNode anchorNode, TransformableNode model, String name) {
        ViewRenderable.builder().setView(this, R.layout.name_card)
                .build().thenAccept(viewRenderable -> {
            TransformableNode nameView = new TransformableNode(arFragment.getTransformationSystem());
            nameView.setLocalPosition(new Vector3(0f, model.getLocalPosition().y+0.1f, 0));
            nameView.setParent(anchorNode);
            nameView.setRenderable(viewRenderable);
            nameView.select();
            TextView tv_animalName =(TextView) viewRenderable.getView();
            tv_animalName.setText(name);
            tv_animalName.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    anchorNode.setParent(null);
                }
            });
        });
    }

    private void setClickListener() {
        for (View view : arrayView) {
            view.setOnClickListener(this);
        }
    }
    private void setArrayView() {
        arrayView = new View[]{bear, cat};
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.bear){
            selected = 1;
            setBackground(v.getId());
        }
        else if(v.getId() == R.id.cat){
            selected = 2;
            setBackground(v.getId());
        }
    }

    private void setBackground(int id) {
        for (View view : arrayView) {
            if (view.getId() == id) {
                view.setBackgroundColor(Color.parseColor("#80333639"));
            } else {
                view.setBackgroundColor(Color.TRANSPARENT);
            }
        }
    }
}
