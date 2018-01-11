package lbstest.example.com.hellocharttest;


import android.renderscript.Sampler;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import lecho.lib.hellocharts.animation.ChartAnimationListener;
import lecho.lib.hellocharts.gesture.ZoomType;
import lecho.lib.hellocharts.listener.LineChartOnValueSelectListener;
import lecho.lib.hellocharts.model.Axis;
import lecho.lib.hellocharts.model.Line;
import lecho.lib.hellocharts.model.LineChartData;
import lecho.lib.hellocharts.model.PointValue;
import lecho.lib.hellocharts.model.ValueShape;
import lecho.lib.hellocharts.model.Viewport;
import lecho.lib.hellocharts.util.ChartUtils;
import lecho.lib.hellocharts.view.LineChartView;

public class LineChartActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_line_chart);
        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction().add(R.id.container, new PlaceholderFragment()).commit();
        }
    }
    public static class PlaceholderFragment extends Fragment {
        private LineChartView chart;
        private LineChartData data;
        private int numberOfLines = 1;
        private int maxNumberOfLines = 4;
        private int numberOfPoints = 12;

        float[][] randomNumbersTab = new float[maxNumberOfLines][numberOfPoints];

        private boolean hasAxes = true;
        private boolean hasAxesNames = true;
        private boolean hasLines = true;
        private boolean hasPoints = true;
        private ValueShape shape = ValueShape.CIRCLE;
        private boolean isFilled = false;
        private boolean hasLabels = false;
        private boolean isCubic = false;
        private boolean hasLabelForSelected = false;
        private boolean pointsHaveDifferentColor;
        private boolean hasGradientToTransparent = false;

        public PlaceholderFragment(){}

        @Nullable
        @Override
        public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
            setHasOptionsMenu(true);
            View rootView = inflater.inflate(R.layout.fragment_line_chart,container,false);

            chart = rootView.findViewById(R.id.chart);
            chart.setOnValueTouchListener(new ValueTouchListener());

            generateValues();

            generateData();
            chart.setViewportCalculationEnabled(false);
            resetViewPort();
            return rootView;
        }

        @Override
        public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
            inflater.inflate(R.menu.line_chart,menu);
        }

        @Override
        public boolean onOptionsItemSelected(MenuItem item) {
            int id = item.getItemId();
            if (id == R.id.action_reset){
                reset();
                generateData();
                return true;
            }
            if (id == R.id.action_add_line) {
                addLineToData();
                return true;
            }
            if (id == R.id.action_toggle_lines) {
                toggleLines();
                return true;
            }
            if (id == R.id.action_toggle_points) {
                togglePoints();
                return true;
            }
            if (id == R.id.action_toggle_gradient) {
                toggleGradient();
                return true;
            }
            if (id == R.id.action_toggle_cubic) {
                toggleCubic();
                return true;
            }
            if (id == R.id.action_toggle_area) {
                toggleFilled();
                return true;
            }
            if (id == R.id.action_point_color) {
                togglePointColor();
                return true;
            }
            if (id == R.id.action_shape_circles) {
                setCircles();
                return true;
            }
            if (id == R.id.action_shape_square) {
                setSquares();
                return true;
            }
            if (id == R.id.action_shape_diamond) {
                setDiamonds();
                return true;
            }
            if (id == R.id.action_toggle_labels) {
                toggleLabels();
                return true;
            }
            if (id == R.id.action_toggle_axes) {
                toggleAxes();
                return true;
            }
            if (id == R.id.action_toggle_axes_names) {
                toggleAxesNames();
                return true;
            }
            if (id == R.id.action_animate) {
                prepareDataAnimation();
                chart.startDataAnimation();
                return true;
            }
            if (id == R.id.action_toggle_selection_mode) {
                toggleLabelForSelected();

                Toast.makeText(getActivity(),
                        "Selection mode set to " + chart.isValueSelectionEnabled() + " select any point.",
                        Toast.LENGTH_SHORT).show();
                return true;
            }
            if (id == R.id.action_toggle_touch_zoom) {
                chart.setZoomEnabled(!chart.isZoomEnabled());
                Toast.makeText(getActivity(), "IsZoomEnabled " + chart.isZoomEnabled(), Toast.LENGTH_SHORT).show();
                return true;
            }
            if (id == R.id.action_zoom_both) {
                chart.setZoomType(ZoomType.HORIZONTAL_AND_VERTICAL);
                return true;
            }
            if (id == R.id.action_zoom_horizontal) {
                chart.setZoomType(ZoomType.HORIZONTAL);
                return true;
            }
            if (id == R.id.action_zoom_vertical) {
                chart.setZoomType(ZoomType.VERTICAL);
                return true;
            }
            return super.onOptionsItemSelected(item);
        }

        private void generateValues(){
            for (int i=0;i<maxNumberOfLines;++i){
                for (int j=0;j<numberOfPoints;++j){
                    randomNumbersTab[i][j] = (float) (Math.random()*100f);
                }
            }
        }

        private void reset(){
            numberOfLines =1;

            hasAxes = true;
            hasAxesNames = true;
            hasLines = true;
            hasPoints = true;
            shape = ValueShape.CIRCLE;
            isFilled = false;
            hasLabels = false;
            isCubic = false;
            hasLabelForSelected = false;
            pointsHaveDifferentColor = false;

            chart.setValueTouchEnabled(hasLabelForSelected);
            resetViewPort();
        }

        private void resetViewPort(){
            final Viewport v = new Viewport(chart.getMaximumViewport());
            v.bottom = 0;
            v.top = 100;
            v.left = 0;
            v.right = numberOfPoints - 1;
            chart.setMaximumViewport(v);
            chart.setCurrentViewport(v);
        }

        private void generateData() {

            List<Line> lines = new ArrayList<Line>();
            for (int i = 0; i < numberOfLines; ++i) {

                List<PointValue> values = new ArrayList<PointValue>();
                for (int j = 0; j < numberOfPoints; ++j) {
                    values.add(new PointValue(j, randomNumbersTab[i][j]));
                }

                Line line = new Line(values);
                line.setColor(ChartUtils.COLORS[i]);
                line.setShape(shape);
                line.setCubic(isCubic);
                line.setFilled(isFilled);
                line.setHasLabels(hasLabels);
                line.setHasLabelsOnlyForSelected(hasLabelForSelected);
                line.setHasLines(hasLines);
                line.setHasPoints(hasPoints);
//                line.setHasGradientToTransparent(hasGradientToTransparent);
                if (pointsHaveDifferentColor){
                    line.setPointColor(ChartUtils.COLORS[(i+1)%ChartUtils.COLORS.length]);
                }
                lines.add(line);
            }
            data = new LineChartData(lines);
            if (hasAxes){
                Axis axisX = new Axis();
                Axis axisY = new Axis().setHasLines(true);
                if (hasAxesNames){
                    axisX.setName("Axis X");
                    axisY.setName("Axis Y");
                }
                data.setAxisXBottom(axisX);
                data.setAxisYLeft(axisY);
            }else {
                   data.setAxisXBottom(null);
                   data.setAxisYLeft(null);
            }
            data.setBaseValue(Float.NEGATIVE_INFINITY);
            chart.setLineChartData(data);
        }


        private void addLineToData(){
            if (data.getLines().size() >= maxNumberOfLines){
                Toast.makeText(getActivity(),"Samples app uses max 4 lines!",Toast.LENGTH_LONG).show();
                return;
            }else {
                ++numberOfLines;
            }
            generateData();
        }

        private void toggleLines() {
            hasLines = !hasLines;

            generateData();
        }

        private void togglePoints(){
            hasPoints = !hasPoints;
            generateData();
        }

        private void toggleGradient(){
            hasGradientToTransparent = !hasGradientToTransparent;
            generateData();
        }

        private void toggleCubic(){
            isCubic = !isCubic;
            generateData();

            if (isCubic){
                final Viewport v = new Viewport(chart.getMaximumViewport());
                v.bottom = -5;
                v.top = 105;
                chart.setMaximumViewport(v);
                chart.setCurrentViewportWithAnimation(v);
            }else{
                final Viewport v = new Viewport(chart.getMaximumViewport());
                v.bottom = 0;
                v.top = 100;
                chart.setViewportAnimationListener(new ChartAnimationListener() {
                    @Override
                    public void onAnimationStarted() {

                    }

                    @Override
                    public void onAnimationFinished() {
                        chart.setMaximumViewport(v);
                        chart.setViewportAnimationListener(null);
                    }
                });

                chart.setCurrentViewportWithAnimation(v);
            }
        }

        private void toggleFilled(){
            isFilled = !isFilled;
            generateData();
        }
        private void togglePointColor() {
            pointsHaveDifferentColor = !pointsHaveDifferentColor;

            generateData();
        }
        private void setCircles() {
            shape = ValueShape.CIRCLE;

            generateData();
        }
        private void setSquares() {
            shape = ValueShape.SQUARE;

            generateData();
        }

        private void setDiamonds() {
            shape = ValueShape.DIAMOND;

            generateData();
        }
        private void toggleLabels() {
            hasLabels = !hasLabels;

            if (hasLabels) {
                hasLabelForSelected = false;
                chart.setValueSelectionEnabled(hasLabelForSelected);
            }

            generateData();
        }
        private void toggleLabelForSelected() {
            hasLabelForSelected = !hasLabelForSelected;

            chart.setValueSelectionEnabled(hasLabelForSelected);

            if (hasLabelForSelected) {
                hasLabels = false;
            }

            generateData();
        }
        private void toggleAxes() {
            hasAxes = !hasAxes;

            generateData();
        }
        private void toggleAxesNames() {
            hasAxesNames = !hasAxesNames;

            generateData();
        }

        private void prepareDataAnimation(){
            for (Line line :data.getLines()){
                for (PointValue value : line.getValues()){
                    value.setTarget(value.getX(),(float)Math.random()*100);
                }
            }
        }

        private class ValueTouchListener implements LineChartOnValueSelectListener{

            @Override
            public void onValueSelected(int lineIndex, int pointIndex, PointValue value) {
                Toast.makeText(getActivity(),"Selected:" +value,Toast.LENGTH_LONG).show();
            }

            @Override
            public void onValueDeselected() {

            }
        }

    }


}
