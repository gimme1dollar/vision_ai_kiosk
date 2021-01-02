# Viosk
폭 넓은 사용층을 고려한 AI-기반의 **비전인식 키오스크**     

## 개요
소개 영상 (3분) - https://drive.google.com/file/d/1KaQiv4moTRgdAZZi3DC1T_f7XEgCQWQI/view?usp=sharing   
(소개 영상 이미지 출처 - ./files/_image_reference.txt)

## Goal
1) 코로나-19 시대 이후 비접촉 무인 시스템의 활성화로 인해 더 많아진 터치스크린 기반 키오스크의
2) 사용을 어려워하는 글자를 읽는 데에 어려움이 있으시는 분, 높이가 맞지 않아 터치가 힘드신 분, 휠체어를 타시는 분들을 위해
3) 비전 인식 AI를 활용해 더 나은 사용 환경을 제공하는 키오스크

## Project
![구조도](https://user-images.githubusercontent.com/20160685/103462581-8bf62000-4d69-11eb-97fe-52b248cfe66f.png)
프로젝트 구조

### Structure
#### _files
회의록, 신청서, 시연 영상 링크 등 프로젝트 관련 기타 파일.
#### firmware   
STM B-L4S5I-IOT01A 펌웨어.
#### hardware_3dCAD
하드웨어 3D 도면 및 Cubicon 3D 프린터 전용 출력 파일. STL 파일을 통해 다른 CAD 툴에 import 가능.
#### software
Keras 기반의 연령 감지 모델 및 Tensorflow Lite API를 통한 8bit Quantized model 출력 코드. 
#### miscs
라즈베리파이 코드.
#### 작업사진
2021 서울 하드웨어해커톤 Team Viosk 작업 사진.

## Development
### STM32
- STM-B-L4S5I-IOT01A
- STM32Cube-MX
- STM32Cube-AI

### Hardware 
- (3D 모델링/프린팅)
- (갤럭시 탭)

### Firmware
- Rasberry Pi

### Software
- Keras 
- Android Studio
   
   
## Test
프로토타입 - (프로토타입 사진)   
시연 영상 - (시연 영상 URL)

### Age Detection

### Height Measurement 

### Screen Adjustment

### UI Diversification


