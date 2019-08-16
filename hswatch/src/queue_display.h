#include <Arduino.h>
#define BUFFER_SIZE 100

extern QueueHandle_t * queue_display;

typedef enum type_message {_clear, _display, _setPixel, _drawLine, _drawRect, _fillRect, _drawCircle, _fillCircle, _drawHorizontalLine, _drawVerticalLine, _drawProgressBar, _drawFastImage, _drawXbm, _drawString, _drawStringMaxWidth, _getStringWidth, _setTextAlignment, _setFont} type_message;


typedef struct msg_queue_display{
    type_message type;
    int a, b, c, d, e;
    unsigned char* image;
    char s[BUFFER_SIZE];
} msg_queue_display;