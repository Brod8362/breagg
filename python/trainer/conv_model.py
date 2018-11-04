import sys
from tensorflow.contrib import lite

converter = lite.TocoConverter.from_saved_model("C:/breagg/model")
tflite_model = converter.convert()
open("converted_model.tflite", "wb").write(tflite_model)