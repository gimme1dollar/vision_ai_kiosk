from datetime import datetime
from PIL import Image, ImageOps
import os
import serial
import numpy as np

# Open the serial port
ser = serial.Serial("/dev/serial0", baudrate=2000000, rtscts=True)

try:
    while True:
        for a in ser.read():
            while(os.system('raspistill -w 200 -h 200 -t 1 -o ./img_logs/image.jpg')):
                pass
            t = datetime.now().strftime('%m_%d_%H_%M_%S')

            # Read the taken image and convert it to grayscale
            img_path = "./img_logs/image.jpg"
            img = Image.open(img_path)
            img = img.resize((200,200))
            img = ImageOps.grayscale(img)

            # Save the image with timestamp for debugging
            img_path = f"./img_logs/img_{t}.jpg"
            img.save(img_path)
            print(f"Image is saved to " + img_path)

            # Send it to STM
            img = np.array(img)
            #np.save(img_path, img, allow_pickle=False)

            data = list(img.flatten())
            n = ser.write(bytes(data))
            print(f"Sent {n} bytes")


finally:
    ser.close()
