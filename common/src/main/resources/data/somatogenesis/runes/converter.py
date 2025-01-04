from PIL import Image
import os, json

for file in os.listdir("./raw"):
    positions = []
    im : Image.Image = Image.open("./raw/" + file)
    im.convert("RGBA")
    for x in range(im.size[0]):
        for y in range(im.size[1]):
            if im.getpixel((x, y))[3] != 0:
                positions.append([x - im.size[0] // 2, y - im.size[1] // 2])
    with open("./" + file.replace(".png",".json"), "w" if os.path.exists("./" + file.replace(".png",".json")) else "x") as outFile:
        outFile.write(json.dumps({"positions": positions}))