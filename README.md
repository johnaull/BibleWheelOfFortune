# SpinningWheelAndroid

![1]

## Attributes
* wheel_colors        -> Color value
* wheel_stroke_color  -> Color value
* wheel_stroke_width  -> Dimension value
* wheel_items         -> Array of string
* wheel_text_size     -> Dimension value
* wheel_text_color    -> Color value
* wheel_arrow_color   -> Color value
* wheel_arrow_width   -> Dimension value
* wheel_arrow_height  -> Dimension value

## Example
1) Custom view in xml
```xml
<com.gicthus.bibleWheelOfFortune.SpinningWheelView
        android:id="@+id/wheel"
        android:layout_width="match_parent"
        android:layout_height="match_parent"
        android:layout_weight="1"
        app:wheel_arrow_color="@android:color/black"
        app:wheel_colors="@array/rainbow_dash"
        app:wheel_items="@array/dummy"
        app:wheel_stroke_color="@android:color/black"
        app:wheel_stroke_width="5dp"/>
```

2) Custom view in java
```java
public class MainActivity extends AppCompatActivity implements SpinningWheelView.OnRotationListener<String> {

        ...
        
        @Override
        protected void onCreate(Bundle savedInstanceState) {
                super.onCreate(savedInstanceState);
                
                ...

                SpinningWheelView wheelView = (SpinningWheelView) findViewById(R.id.wheel);

                // Can be array string or list of object
                wheelView.setItems(R.array.dummy);
                
                // Set listener for rotation event
                wheelView.setOnRotationListener(new OnRotationListener<String>() {
                        // Call once when start rotation
                        @Override
                        public void onRotation() {
                        Log.d("XXXX", "On Rotation");
                        }

                        // Call once when stop rotation
                        @Override
                        public void onStopRotation(String item) {
                        Log.d("XXXX", "On Rotation");
                        }
                });
                
                // If true: user can rotate by touch
                // If false: user can not rotate by touch
                wheelView.setEnabled(false);
                
                rotate.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        // max angle 50
                        // duration 10 second
                        // every 50 ms rander rotation
                        wheelView.rotate(50, 3000, 50);
                    }
                });
        }

```

## Download
Configure your project-level build.gradle to include the 'android-apt' plugin:
```gradle
repositories {
    maven {
        url 'https://dl.bintray.com/adef145/maven/'
    }
}

...

dependencies {
    compile 'com.gicthus.bibleWheelOfFortune:spinningwheel:0.1.0'
}
```


## Licence
```
Copyright 2017 Ade Fruandta

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
```

[1]: ./example/1.gif
