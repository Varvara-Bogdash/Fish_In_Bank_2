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

public class PieChartView extends View {
    private Paint paint;
    private Paint textPaint;
    private Paint legendPaint;
    private Paint titlePaint;
    private int[] colors = {
            Color.rgb(220, 100, 120),  // Более темный розовый
            Color.rgb(100, 120, 220),  // Более темный голубой
            Color.rgb(100, 180, 100),  // Более темный зеленый
            Color.rgb(220, 180, 100),  // Более темный персиковый
            Color.rgb(180, 100, 180),  // Более темный фиолетовый
            Color.rgb(100, 180, 220),  // Более темный бирюзовый
            Color.rgb(220, 120, 130),  // Более темный розовый 2
            Color.rgb(120, 200, 120),  // Более темный салатовый
            Color.rgb(220, 160, 110),  // Более темный абрикосовый
            Color.rgb(160, 110, 180)   // Более темный лавандовый
    };
    private DecimalFormat df = new DecimalFormat("#.#");

    public PieChartView(Context context) {
        super(context);
        init();
    }

    public PieChartView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public PieChartView(Context context, AttributeSet attrs, int defStyleAttr) {
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
        legendPaint.setTextSize(40f); // Слегка уменьшим для лучшего отображения

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

        if (DataManager.expensesByCategory == null || DataManager.expensesByCategory.isEmpty()) {
            drawEmptyChart(canvas);
            return;
        }

        double totalExpenses = 0.0;
        for (double value : DataManager.expensesByCategory.values()) {
            totalExpenses += value;
        }

        if (totalExpenses == 0.0) {
            drawEmptyChart(canvas);
            return;
        }

        // Рисуем диаграмму
        float startAngle = 0f;
        int colorIndex = 0;
        float chartSize = Math.min(getWidth(), getHeight()) * 0.45f; // Уменьшим диаграмму для легенды
        float centerX = getWidth() / 2f;
        float centerY = getHeight() / 4f; // Смещаем выше для места под легенду

        RectF rect = new RectF(
                centerX - chartSize/2,
                centerY - chartSize/2,
                centerX + chartSize/2,
                centerY + chartSize/2
        );

        // Рисуем сегменты диаграммы
        for (Map.Entry<String, Double> entry : DataManager.expensesByCategory.entrySet()) {
            String category = entry.getKey();
            double value = entry.getValue();
            float sweepAngle = (float) (value / totalExpenses * 360);
            double percentage = (value / totalExpenses) * 100;

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
        drawLegend(canvas, totalExpenses, centerY + chartSize/2 + 30f);

        // Заголовок диаграммы перенесен вниз
        canvas.drawText("Расходы по категориям", centerX, getHeight() - 80f, titlePaint);
    }

    private void drawLegend(Canvas canvas, double totalExpenses, float startY) {
        float legendX = 40f;
        float legendY = startY;
        float rectSize = 35f; // Уменьшим немного
        float textOffset = 50f; // Уменьшим немного
        float lineHeight = 55f; // Уменьшим немного для большего количества строк
        int maxWidth = getWidth() - 100;

        int colorIndex = 0;
        int itemsInColumn = 0;
        int maxItems = DataManager.expensesByCategory.size();

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

        for (Map.Entry<String, Double> entry : DataManager.expensesByCategory.entrySet()) {
            String category = entry.getKey();
            double value = entry.getValue();
            double percentage = (value / totalExpenses) * 100;

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
            itemsInColumn++;
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
        canvas.drawText("Нет данных о расходах", centerX, centerY, emptyTextPaint);

        // Заголовок перенесен вниз
        canvas.drawText("Расходы по категориям", centerX, getHeight() - 80f, titlePaint);
    }
}