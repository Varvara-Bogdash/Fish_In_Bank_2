package com.example.fishinbank;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.util.Log;
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
    private float scaleFactor = 1.0f; // Коэффициент масштабирования для адаптации

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

        // Рассчитываем масштабный коэффициент на основе размера легенды
        calculateScaleFactor();

        // Применяем масштабирование к размерам шрифтов
        float currentLegendTextSize = 40f * scaleFactor;
        legendPaint.setTextSize(currentLegendTextSize);

        // Рисуем легенду слева и получаем ее ширину
        float legendWidth = drawLegendLeft(canvas, totalIncomes);

        // Увеличиваем диаграмму - изменен коэффициент с 0.55f на 0.65f
        float chartSize = Math.min(getWidth() - legendWidth, getHeight()) * 0.65f * scaleFactor;
        float centerX = legendWidth + (getWidth() - legendWidth) / 2f + 30f; // Смещение диаграммы вправо
        float centerY = getHeight() / 4f;

        RectF rect = new RectF(
                centerX - chartSize/2,
                centerY - chartSize/2,
                centerX + chartSize/2,
                centerY + chartSize/2
        );

        // Рисуем сегменты диаграммы
        drawPieChart(canvas, totalIncomes, rect, centerX, centerY, chartSize);

        // Заголовок диаграммы
        canvas.drawText("Доходы по категориям", getWidth() / 2f, getHeight() - 80f, titlePaint);
    }

    private void calculateScaleFactor() {
        if (DataManager.incomesByCategory == null) return;

        int itemCount = DataManager.incomesByCategory.size();
        float maxHeight = getHeight() * 0.8f; // Максимальная доступная высота
        float lineHeight = 55f; // Базовая высота строки

        // Рассчитываем необходимую высоту для легенды
        float neededHeight = itemCount * lineHeight;

        // Если легенда слишком большая, уменьшаем масштаб
        if (neededHeight > maxHeight) {
            scaleFactor = maxHeight / neededHeight;
            // Ограничиваем минимальный масштаб - увеличено с 0.6f до 0.7f
            scaleFactor = Math.max(scaleFactor, 0.7f);
        } else {
            scaleFactor = 1.0f;
        }

        // Также проверяем ширину
        float maxWidth = getWidth() * 0.4f; // Максимальная ширина для легенды
        if (maxWidth < getWidth() * 0.3f) {
            scaleFactor = Math.min(scaleFactor, 0.8f);
        }
    }

    private float drawLegendLeft(Canvas canvas, double totalIncomes) {
        if (DataManager.incomesByCategory == null || DataManager.incomesByCategory.isEmpty()) {
            return 0;
        }

        float legendX = 20f;
        float legendY = 40f;
        float rectSize = 35f * scaleFactor;
        float textOffset = 50f * scaleFactor;
        float lineHeight = 55f * scaleFactor;

        // Рассчитываем максимальную ширину текста в легенде
        float maxTextWidth = 0;
        float currentLegendTextSize = legendPaint.getTextSize();

        for (Map.Entry<String, Double> entry : DataManager.incomesByCategory.entrySet()) {
            String category = entry.getKey();
            double value = entry.getValue();
            double percentage = (value / totalIncomes) * 100;
            String legendText = category + " (" + df.format(percentage) + "%)";
            float textWidth = legendPaint.measureText(legendText);
            maxTextWidth = Math.max(maxTextWidth, textWidth);
        }

        // Ширина легенды = отступ + размер прямоугольника + отступ текста + ширина текста
        // Увеличено с 20f до 40f для более широкого отступа
        float legendWidth = legendX + rectSize + textOffset + maxTextWidth + 40f;

        // Увеличиваем доступную ширину для текста до 45% ширины экрана
        float maxAllowedLegendWidth = getWidth() * 0.45f;
        if (legendWidth > maxAllowedLegendWidth) {
            // Если легенда слишком широкая, уменьшаем масштаб для ширины
            float widthScale = maxAllowedLegendWidth / legendWidth;
            scaleFactor = Math.min(scaleFactor, widthScale);

            // Пересчитываем размеры с новым scaleFactor
            currentLegendTextSize = 40f * scaleFactor;
            legendPaint.setTextSize(currentLegendTextSize);
            rectSize = 35f * scaleFactor;
            textOffset = 50f * scaleFactor;
            lineHeight = 55f * scaleFactor;
            legendWidth = legendX + rectSize + textOffset + maxTextWidth * scaleFactor + 40f;
        }

        int colorIndex = 0;

        for (Map.Entry<String, Double> entry : DataManager.incomesByCategory.entrySet()) {
            String category = entry.getKey();
            double value = entry.getValue();
            double percentage = (value / totalIncomes) * 100;

            // Цветной прямоугольник
            paint.setColor(colors[colorIndex % colors.length]);
            canvas.drawRect(legendX, legendY, legendX + rectSize, legendY + rectSize, paint);

            // Текст категории и процента - теперь всегда полный текст
            String legendText = category + " (" + df.format(percentage) + "%)";

            // Убрано обрезание текста - теперь всегда отображается полное название
            canvas.drawText(legendText, legendX + textOffset,
                    legendY + rectSize/2 + currentLegendTextSize/3, legendPaint);

            legendY += lineHeight;
            colorIndex++;
        }

        return legendWidth;
    }

    private void drawPieChart(Canvas canvas, double totalIncomes, RectF rect,
                              float centerX, float centerY, float chartSize) {
        float startAngle = 0f;
        int colorIndex = 0;

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
        // Увеличена диаграмма при отсутствии данных с 0.5f до 0.6f
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

        // Сообщение об отсутствии данных
        Paint emptyTextPaint = new Paint();
        emptyTextPaint.setAntiAlias(true);
        emptyTextPaint.setColor(Color.YELLOW);
        emptyTextPaint.setTextSize(30f);
        emptyTextPaint.setTextAlign(Paint.Align.CENTER);
        canvas.drawText("Нет данных о доходах", centerX, centerY, emptyTextPaint);

        // Заголовок
        canvas.drawText("Доходы по категориям", centerX, getHeight() - 80f, titlePaint);
    }
}