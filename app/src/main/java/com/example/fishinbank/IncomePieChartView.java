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
            Color.rgb(220, 100, 120)   // Темный розовый 2
    };
    private DecimalFormat df = new DecimalFormat("#.#");

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

        if (DataManager.incomesByCategory == null || DataManager.incomesByCategory.isEmpty()) {
            drawEmptyChart(canvas);
            return;
        }

        double totalIncomes = 0.0;
        for (double value : DataManager.incomesByCategory.values()) {
            totalIncomes += value;
        }

        if (totalIncomes == 0.0) {
            drawEmptyChart(canvas);
            return;
        }

        // Рисуем диаграмму
        float startAngle = 0f;
        int colorIndex = 0;
        float chartSize = Math.min(getWidth(), getHeight()) * 0.45f;
        float centerX = getWidth() / 2f;
        float centerY = getHeight() / 4f;

        RectF rect = new RectF(
                centerX - chartSize/2,
                centerY - chartSize/2,
                centerX + chartSize/2,
                centerY + chartSize/2
        );

        // Рисуем сегменты диаграммы
        for (Map.Entry<String, Double> entry : DataManager.incomesByCategory.entrySet()) {
            String category = entry.getKey();
            double value = entry.getValue();
            float sweepAngle = (float) (value / totalIncomes * 360);
            double percentage = (value / totalIncomes) * 100;

            paint.setColor(colors[colorIndex % colors.length]);
            canvas.drawArc(rect, startAngle, sweepAngle, true, paint);

            // Добавляем процент в центр сегмента (если сегмент достаточно большой)
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

        // Рисуем легенду
        drawLegend(canvas, totalIncomes, centerY + chartSize/2 + 30f);

        // Заголовок диаграммы перенесен вниз
        canvas.drawText("Доходы по категориям", centerX, getHeight() - 80f, titlePaint);
    }

    private void drawLegend(Canvas canvas, double totalIncomes, float startY) {
        float legendX = 40f;
        float legendY = startY;
        float rectSize = 35f;
        float textOffset = 50f;
        float lineHeight = 55f;
        int maxWidth = getWidth() - 100;

        int colorIndex = 0;
        int maxItems = DataManager.incomesByCategory.size();

        // Вычисляем, сколько колонок потребуется
        int columns = 1;
        float availableHeight = getHeight() - startY - 150f;
        float neededHeight = maxItems * lineHeight;

        if (neededHeight > availableHeight && maxItems > 0) {
            columns = (int) Math.ceil(neededHeight / availableHeight);
        }

        float columnWidth = (maxWidth + 40f) / columns;
        int itemsPerColumn = (int) Math.ceil((float) maxItems / columns);

        float currentColumnX = legendX;
        int currentItemInColumn = 0;

        for (Map.Entry<String, Double> entry : DataManager.incomesByCategory.entrySet()) {
            String category = entry.getKey();
            double value = entry.getValue();
            double percentage = (value / totalIncomes) * 100;

            // Переходим на новую колонку при необходимости
            if (currentItemInColumn >= itemsPerColumn) {
                currentColumnX += columnWidth;
                legendY = startY;
                currentItemInColumn = 0;
            }

            // Цветной прямоугольник
            paint.setColor(colors[colorIndex % colors.length]);
            canvas.drawRect(currentColumnX, legendY, currentColumnX + rectSize, legendY + rectSize, paint);

            // Текст категории и процента
            String legendText = category + " (" + df.format(percentage) + "%)";

            // Обрезаем текст если слишком длинный
            float availableTextWidth = columnWidth - rectSize - textOffset - 10f;
            if (legendPaint.measureText(legendText) > availableTextWidth) {
                legendText = shortenText(legendPaint, legendText, availableTextWidth);
            }

            canvas.drawText(legendText, currentColumnX + textOffset, legendY + 22f, legendPaint);

            legendY += lineHeight;
            colorIndex++;
            currentItemInColumn++;
        }
    }

    private String shortenText(Paint paint, String text, float maxWidth) {
        if (paint.measureText(text) <= maxWidth) {
            return text;
        }

        int endIndex = text.length() - 1;
        while (endIndex > 3 && paint.measureText(text.substring(0, endIndex) + "...") > maxWidth) {
            endIndex--;
        }

        return text.substring(0, endIndex) + "...";
    }

    private void drawEmptyChart(Canvas canvas) {
        float centerX = getWidth() / 2f;
        float centerY = getHeight() / 3f;
        float chartSize = Math.min(getWidth(), getHeight()) * 0.45f;

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

        // Сообщение об отсутствии данных
        Paint emptyTextPaint = new Paint();
        emptyTextPaint.setAntiAlias(true);
        emptyTextPaint.setColor(Color.YELLOW);
        emptyTextPaint.setTextSize(30f);
        emptyTextPaint.setTextAlign(Paint.Align.CENTER);
        canvas.drawText("Нет данных о доходах", centerX, centerY, emptyTextPaint);

        // Заголовок перенесен вниз
        canvas.drawText("Доходы по категориям", centerX, getHeight() - 80f, titlePaint);
    }
}