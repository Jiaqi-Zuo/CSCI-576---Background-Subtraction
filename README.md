# Background subtraction from static camera video

## Overview
Reading images as .rgb files and processed the video in two modes: 
  - In mode 0, the video does not have any constant colored green screen and the program should be able to arrive at the green screen pixels by comparing two frames where pixels that are constant (not changing within some threshold) can be assumed to be “green screen” pixels and hence can be replaced by the corresponding pixels in the given replacement background video. This algorithm is known as **background subtraction**.
  - In mode 1, the video has green screen as background, the program just need to replaced the corresponding green pixels with replacement background.

### Compile instruction
```
YourProgram.exe C:/myDir/foreGroundVideo C:/myDir/backGroundVideo mode
```
- foreGroundVideo is the base name for a green screened foreground video, which has a foreground element (actor, object) captured in front of a green screen.
- backGroundVideo is any normal video
- mode is a mode that can take values 1 or 0. 1 indicating that the foreground video has a green screen, and 0 indicating there is no green screen.

<sub> mode 0, no green screen provided. </sub>  
![](https://github.com/Jiaqi-Zuo/CSCI-576---Background-Subtraction/blob/0f510b47c73b9f11ec269d3bafc775c92c86b5ec/bgremove.gif)
      

