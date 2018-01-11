package lbstest.example.com.hellocharttest;

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

import java.util.ArrayList;
import java.util.List;

import lecho.lib.hellocharts.gesture.ZoomType;
import lecho.lib.hellocharts.listener.ViewportChangeListener;
import lecho.lib.hellocharts.model.Axis;
import lecho.lib.hellocharts.model.Line;
import lecho.lib.hellocharts.model.LineChartData;
import lecho.lib.hellocharts.model.PointValue;
import lecho.lib.hellocharts.model.Viewport;
import lecho.lib.hellocharts.util.ChartUtils;
import lecho.lib.hellocharts.view.LineChartView;
import lecho.lib.hellocharts.view.PreviewLineChartView;

public class PreviewLineChartActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_preview_line_chart);

        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction().add(R.id.container, new PlaceholderFragment()).commit();
        }
    }

    public static class PlaceholderFragment extends Fragment{
        private LineChartView chart;
        private PreviewLineChartView previewChart;
        private LineChartData data;

        private LineChartData previewData;

        public PlaceholderFragment(){

        }

        @Nullable
        @Override
        public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
            setHasOptionsMenu(true);
            View rootView = inflater.inflate(R.layout.fragment_preview_line_chart,container,false);

            chart = rootView.findViewById(R.id.chart);
            previewChart = rootView.findViewById(R.id.chart_preview);

            generateDefaultData();

            chart.setLineChartData(data);
            chart.setZoomEnabled(false);
            chart.setScrollEnabled(false);

            previewChart.setLineChartData(previewData);
            previewChart.setViewportChangeListener(new ViewportListener());

            previewX(false);
                return rootView;
        }

        @Override
        public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
                inflater.inflate(R.menu.preview_line_chart,menu);
        }

        @Override
        public boolean onOptionsItemSelected(MenuItem item) {

            int id  = item.getItemId();
            if (id == R.id.action_reset){
                generateDefaultData();
                chart.setLineChartData(data);
                previewChart.setLineChartData(previewData);
                previewX(true);
                return true;
            }
            if (id == R.id.action_preview_both){
                previewXY();
                previewChart.setZoomType(ZoomType.HORIZONTAL);
                return true;
            }
            if (id == R.id.action_preview_horizontal){
                previewX(true);
                return true;
            }
            if (id == R.id.action_preview_vertical){
                previewY();
                return true;
            }
            if (id == R.id.action_change_color){
                int color = ChartUtils.pickColor();
                while (color == previewChart.getPreviewColor()){
                    color = ChartUtils.pickColor();
                }
                previewChart.setPreviewColor(color);
                return true;
            }
            return super.onOptionsItemSelected(item);
        }


        private void generateDefaultData(){
            int numValues = 50;

            List<PointValue> values = new ArrayList<>();
            for (int i=0;i<numValues;++i){
                values.add(new PointValue(i,(float)Math.random()*100f));
            }

            Line line = new Line(values);
            line.setColor(ChartUtils.COLOR_GREEN);
            line.setHasPoints(false);

            List<Line> lines = new ArrayList<Line>();
            lines.add(line);

            data = new LineChartData(lines);
            data.setAxisXBottom(new Axis());
            data.setAxisYLeft(new Axis().setHasLines(true));

            previewData = new LineChartData(data);
            previewData.getLines().get(0).setColor(ChartUtils.DEFAULT_DARKEN_COLOR);

        }

        private void previewY(){
            Viewport tempViewport = new Viewport(chart.getMaximumViewport());
            float dy = tempViewport.height()/4; //纵坐标长度
            tempViewport.inset(0,dy);   //设置纵坐标长度
            previewChart.setCurrentViewportWithAnimation(tempViewport);
            previewChart.setZoomType(ZoomType.VERTICAL);
        }

        private void previewX(boolean animate){
            Viewport tempViewport = new Viewport(chart.getMaximumViewport());
            float dx = tempViewport.width()/4;
            tempViewport.inset(dx,0);
            if (animate){
                previewChart.setCurrentViewportWithAnimation(tempViewport);
            }else {
                previewChart.setCurrentViewportWithAnimation(tempViewport);
            }
            previewChart.setZoomType(ZoomType.HORIZONTAL);
        }

        private void previewXY(){
            Viewport tempViewport = new Viewport(chart.getMaximumViewport());   //获得最大视图
            float dx = tempViewport.width()/4;
            float dy = tempViewport.width()/4;
            tempViewport.inset(dx,dy);
            previewChart.setCurrentViewportWithAnimation(tempViewport);
        }

        private class ViewportListener implements ViewportChangeListener{

            @Override
            public void onViewportChanged(Viewport viewport) {

                chart.setCurrentViewport(viewport);
            }
        }

    }

}
