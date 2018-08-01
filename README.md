# PatternLockView
自定义图案锁View

```Java
private PatternLockView patternLockView;
```


## ScreenShot ##
![image](https://github.com/YuanJianTing/PatternLockView/blob/master/screenshot/1.gif)

### Layout example 1 ###
``` xml
 <com.yuan.lock.widget.PatternLockView
        android:id="@+id/lock_view"
        android:layout_width="match_parent"
        android:layout_height="match_parent"
        app:layout_constraintBottom_toBottomOf="parent"
        app:layout_constraintEnd_toEndOf="parent"
        app:layout_constraintStart_toStartOf="parent"
        app:layout_constraintTop_toTopOf="parent"
        app:textSize="14sp"
        app:tips="绘制解锁图案"
        app:radius="35dp"
        app:strokeWidth="2dp"
        app:textColor="@color/lock_view_default_color"
       />
```
### Activity ###
``` java
PatternLockView patternLockView = (PatternLockView) findViewById(R.id.lock_view);
patternLockView.setPatternLockViewListener((values)->{
            StringBuffer sb=new StringBuffer();
            for(int i=0;i<values.length;i++)
                sb.append(values[i]);
            Toast.makeText(MainActivity.this,sb.toString(),Toast.LENGTH_SHORT).show();
        });
```

### attrs ###
``` xml
         <!--圆的半径-->
        <attr name="radius" format="dimension"/>
        <!--圆圈选中后的颜色-->
        <attr name="selectedColor" format="color"/>
        <!--圆圈默认颜色-->
        <attr name="color" format="color"/>
        <!--连接线的颜色-->
        <attr name="lineColor" format="color"/>
        <!--连接线的粗细-->
        <attr name="strokeWidth" format="dimension"/>
        <!--提示信息-->
        <attr name="tips" format="string"/>
        <attr name="textColor" format="color"/>
        <attr name="textSize" format="dimension"/>
```
