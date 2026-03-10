package com.example.fishinbank;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.View;
import java.text.DecimalFormat;
import java.util.Map;

public class IncomePieChartView extends View {
    private Paint paint;
    private Paint textPaint;
    private Paint legendPaint;
    private Paint titlePaint;
    private int[] colors = {
            Color.rgb(100, 180, 100),  // Темный зеленый (доходы)
            Color.rgb(100, 120, 220),  // Темный голубой
            Color.rgb(220, 180, 100),  // Темный персиковый
            Color.rgb(180, 100, 180),  // Темный фиолетовый
            Color.rgb(100, 180, 220),  // Темный бирюзовый
            Color.rgb(220, 120, 130),  // Темный розовый
            Color.rgb(120, 200, 120),  // Темный салатовый
            Color.rgb(220, 160, 110),  // Темный абрикосовый
            Color.rgb(160, 110, 180),  // Темный лавандовый
            Color.rgb(220, 100, 120),  // Темный розовый 2
            Color.rgb(140, 160, 200),  // Серо-голубой
            Color.rgb(200, 140, 160),  // Розовато-коричневый
            Color.rgb(140, 200, 180),  // Мятный
            Color.rgb(200, 180, 140),  // Бежевый
            Color.rgb(160, 140, 200),  // Сиреневый
            Color.rgb(200, 140, 200),  // Пурпурный
            Color.rgb(140, 200, 140),  // Светло-зеленый
            Color.rgb(200, 200, 140),  // Светло-желтый
            Color.rgb(140, 160, 180),  // Стальной
            Color.rgb(180, 140, 160)   // Розово-серый
    };
    private DecimalFormat df = new DecimalFormat("#.#");
    private float scaleFactor = 1.0f;
    private float chartSize = 0;

    // Поля для хранения данных диаграммы
    private float[] values = new float[0];
    private String[] categoryNames = new String[0];
    private boolean useExternalData = false;

    public IncomePieChartView(Context context) {
        super(context);
        init();
    }

    public IncomePieChartView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public IncomePieChartView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        paint = new Paint();
        paint.setAntiAlias(true);
        paint.setStyle(Paint.Style.FILL);

        textPaint = new Paint();
        textPaint.setAntiAlias(true);
        textPaint.setColor(Color.YELLOW);
        textPaint.setTextSize(30f);
        textPaint.setTextAlign(Paint.Align.CENTER);

        legendPaint = new Paint();
        legendPaint.setAntiAlias(true);
        legendPaint.setColor(Color.YELLOW);
        legendPaint.setTextSize(40f);

        titlePaint = new Paint();
        titlePaint.setAntiAlias(true);
        titlePaint.setColor(Color.YELLOW);
        titlePaint.setTextSize(40f);
        titlePaint.setTextAlign(Paint.Align.CENTER);
        titlePaint.setFakeBoldText(true);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        // Рассчитываем размер диаграммы один раз, чтобы он был одинаковым
        chartSize = Math.min(getWidth(), getHeight()) * 0.55f;

        // Выбираем источник данных в зависимости от флага
        boolean isEmpty;
        double totalIncomes;
        Map<String, Double> dataMap;


        if (useExternalData && values.length > 0) {
            isEmpty = values.length == 0;
            totalIncomes = calculateTotal(values);
            dataMap = convertToMap();
        } else {
            if (DataManager.incomesByCategory == null || DataManager.incomesByCategory.isEmpty()) {
                drawEmptyChart(canvas);
                return;
            }
            isEmpty = DataManager.incomesByCategory.isEmpty();
            totalIncomes = DataManager.totalIncome;
            dataMap = DataManager.incomesByCategory;
        }

        if (isEmpty || totalIncomes == 0.0) {
            drawEmptyChart(canvas);
            return;
        }

        // Рассчитываем масштабный коэффициент на основе размера легенды
        calculateScaleFactor(dataMap);

        // Применяем масштабирование к размерам шрифтов
        float currentLegendTextSize = 30f * scaleFactor;
        legendPaint.setTextSize(currentLegendTextSize);

        // Рисуем легенду слева и получаем ее ширину
        float legendWidth = drawLegendLeft(canvas, totalIncomes, dataMap);

        // Используем фиксированный размер диаграммы (такой же как в пустой)
        float centerX = legendWidth + (getWidth() - legendWidth) / 2f + 70f;
        float centerY = getHeight() / 3f;

        RectF rect = new RectF(
                centerX - chartSize/2,
                centerY - chartSize/2,
                centerX + chartSize/2,
                centerY + chartSize/2
        );

        // Рисуем сегменты диаграммы
        drawPieChart(canvas, totalIncomes, rect, centerX, centerY, chartSize, dataMap);

        // Заголовок диаграммы
        canvas.drawText("Доходы по категориям", getWidth() / 2f, getHeight() - 80f, titlePaint);
    }

    public void setData(float[] values) {
        if (values == null) {
            this.values = new float[0];
        } else {
            this.values = values.clone();
        }
        useExternalData = true;
        invalidate();
    }

    public void setCategoryNames(String[] categoryNames) {
        if (categoryNames == null) {
            this.categoryNames = new String[0];
        } else {
            this.categoryNames = categoryNames.clone();
        }
        invalidate();
    }
    private void calculateScaleFactor(Map<String, Double> dataMap) {
        if (dataMap == null) return;

        int itemCount = dataMap.size();
        float maxHeight = getHeight() * 0.8f;
        float lineHeight = 55f;

        float neededHeight = itemCount * lineHeight;

        if (neededHeight > maxHeight) {
            scaleFactor = maxHeight / neededHeight;
            scaleFactor = Math.max(scaleFactor, 0.7f);
        } else {
            scaleFactor = 1.0f;
        }

        float maxWidth = getWidth() * 0.4f;
        if (maxWidth < getWidth() * 0.3f) {
            scaleFactor = Math.min(scaleFactor, 0.8f);
        }
    }

    private float drawLegendLeft(Canvas canvas, double totalIncomes, Map<String, Double> dataMap) {
        if (dataMap == null || dataMap.isEmpty()) {
            return 0;
        }

        float legendX = 20f;
        float legendY = 40f;
        float rectSize = 35f * scaleFactor;
        float textOffset = 50f * scaleFactor;
        float lineHeight = 55f * scaleFactor;

        float maxTextWidth = 0;
        float currentLegendTextSize = legendPaint.getTextSize();

        for (Map.Entry<String, Double> entry : dataMap.entrySet()) {
            String category = entry.getKey();
            double value = entry.getValue();
            double percentage = (value / totalIncomes) * 100;
            String legendText = category + " (" + df.format(percentage) + "%)";
            float textWidth = legendPaint.measureText(legendText);
            maxTextWidth = Math.max(maxTextWidth, textWidth);
        }

        float legendWidth = legendX + rectSize + textOffset + maxTextWidth + 40f;
        float maxAllowedLegendWidth = getWidth() * 0.45f;

        if (legendWidth > maxAllowedLegendWidth) {
            float widthScale = maxAllowedLegendWidth / legendWidth;
            scaleFactor = Math.min(scaleFactor, widthScale);

            currentLegendTextSize = 40f * scaleFactor;
            legendPaint.setTextSize(currentLegendTextSize);
            rectSize = 35f * scaleFactor;
            textOffset = 50f * scaleFactor;
            lineHeight = 55f * scaleFactor;
            legendWidth = legendX + rectSize + textOffset + maxTextWidth * scaleFactor + 40f;
        }

        int colorIndex = 0;

        for (Map.Entry<String, Double> entry : dataMap.entrySet()) {
            String category = entry.getKey();
            double value = entry.getValue();
            double percentage = (value / totalIncomes) * 100;

            paint.setColor(colors[colorIndex % colors.length]);
            canvas.drawRect(legendX, legendY, legendX + rectSize, legendY + rectSize, paint);

            String legendText = category + " (" + df.format(percentage) + "%)";
            canvas.drawText(legendText, legendX + textOffset,
                    legendY + rectSize/2 + currentLegendTextSize/3, legendPaint);

            legendY += lineHeight;
            colorIndex++;
        }

        return legendWidth;
    }

    private void drawPieChart(Canvas canvas, double totalIncomes, RectF rect,
                              float centerX, float centerY, float chartSize, Map<String, Double> dataMap) {
        float startAngle = 0f;
        int colorIndex = 0;

        for (Map.Entry<String, Double> entry : dataMap.entrySet()) {
            String category = entry.getKey();
            double value = entry.getValue();
            float sweepAngle = (float) (value / totalIncomes * 360);
            double percentage = (value / totalIncomes) * 100;

            paint.setColor(colors[colorIndex % colors.length]);
            canvas.drawArc(rect, startAngle, sweepAngle, true, paint);

            if (sweepAngle > 20f) {
                float middleAngle = startAngle + sweepAngle / 2;
                float textRadius = chartSize * 0.3f;
                float textAngle = (float) Math.toRadians(middleAngle);

                float textX = centerX + (float) (textRadius * Math.cos(textAngle));
                float textY = centerY + (float) (textRadius * Math.sin(textAngle));

                String percentText = df.format(percentage) + "%";
                canvas.drawText(percentText, textX, textY, textPaint);
            }

            startAngle += sweepAngle;
            colorIndex++;
        }
    }

    private void drawEmptyChart(Canvas canvas) {
        float centerX = getWidth() / 2f;
        float centerY = getHeight() / 3f;
        float chartSize = Math.min(getWidth(), getHeight()) * 0.6f;

        paint.setColor(Color.LTGRAY);
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(4f);
        RectF rect = new RectF(
                centerX - chartSize/2,
                centerY - chartSize/2,
                centerX + chartSize/2,
                centerY + chartSize/2
        );
        canvas.drawArc(rect, 0f, 360f, false, paint);
        paint.setStyle(Paint.Style.FILL);

        Paint emptyTextPaint = new Paint();
        emptyTextPaint.setAntiAlias(true);
        emptyTextPaint.setColor(Color.YELLOW);
        emptyTextPaint.setTextSize(30f);
        emptyTextPaint.setTextAlign(Paint.Align.CENTER);
        canvas.drawText("Нет данных о доходах", centerX, centerY, emptyTextPaint);

        canvas.drawText("Доходы по категориям", centerX, getHeight() - 80f, titlePaint);
    }

    private double calculateTotal(float[] values) {
        double total = 0.0;
        for (float value : values) {
            total += value;
        }
        return total;
    }

    private Map<String, Double> convertToMap() {
        java.util.HashMap<String, Double> map = new java.util.HashMap<>();

        if (values.length == 0) {
            return map;
        }

        for (int i = 0; i < values.length; i++) {
            String categoryName;
            if (categoryNames.length > i && categoryNames[i] != null && !categoryNames[i].isEmpty()) {
                categoryName = categoryNames[i];
            } else {
                categoryName = "Категория " + (i + 1);
            }
            map.put(categoryName, (double) values[i]);
        }

        return map;
    }
}