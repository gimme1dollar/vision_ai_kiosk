# Viosk
폭 넓은 사용층을 고려한 AI-기반의 **비전인식 키오스크**     

## 개요
![팀원](https://user-images.githubusercontent.com/20160685/103464867-5f96cf80-4d7a-11eb-9195-0ad0df3b8d86.JPG)

#### 소개 영상 
https://drive.google.com/file/d/1KaQiv4moTRgdAZZi3DC1T_f7XEgCQWQI/view?usp=sharing   
(소개 영상 이미지 출처 - ./files/_image_reference.txt)


## Goal
1) 코로나-19 시대 이후 비접촉 무인 시스템의 활성화로 인해 더 많아진 터치스크린 기반 키오스크의
2) 사용을 어려워하는 글자를 읽는 데에 어려움이 있으시는 분, 높이가 맞지 않아 터치가 힘드신 분, 휠체어를 타시는 분들을 위해
3) 비전 인식 AI를 활용해 더 나은 사용 환경을 제공하는 키오스크

## Project
![구조도](https://user-images.githubusercontent.com/20160685/103464856-5279e080-4d7a-11eb-96b9-679e7fc523df.JPG)


## Development
### STM32
- STM-B-L4S5I-IOT01A 
- STM32Cube-MX : STM32 Development Tool
- STM32Cube-AI : STM32 AI 개발

### Hardware 
- Fusion360 : 3D modeling
- Galaxy Tab : mini-screen Kiosk

### Firmware
- Rasberry Pi : 사진 촬영 및 사진 전송 (UART)

### Software
- Keras (ver.2.4.0) : Convolutional Neural Network for Vision Recognition
- Kotlin : Android Application Development

   
## Test
시연 영상 - (시연 영상 URL)


### Age Detection
트레이닝 데이터셋 : https://data.vision.ee.ethz.ch/cvl/rrothe/imdb-wiki/

#### Preprocessing (Age group distribution)
![프리프로세싱](https://user-images.githubusercontent.com/20160685/103465050-94efed00-4d7b-11eb-9887-f54f2f342dcb.JPG)

#### Training/Evaluation (about 93% Accuracy)
![정확도](https://user-images.githubusercontent.com/20160685/103465046-8efa0c00-4d7b-11eb-88bf-2392bb91d23f.JPG)

#### Lightweigrht (below 200KB)
![경량화](https://user-images.githubusercontent.com/20160685/103465267-43e0f880-4d7d-11eb-850c-6569bd7df9bc.JPG)

#### Test on STM32
![테스트on보드](https://user-images.githubusercontent.com/20160685/103465252-2449d000-4d7d-11eb-9c24-b19ad9feac3f.JPG)

### Processing Units
![보드](https://user-images.githubusercontent.com/20160685/103464992-6eca4d00-4d7b-11eb-9632-8b5e38db922c.JPG)

### Height Measurement (초음파 센서)
![초음파](https://user-images.githubusercontent.com/20160685/103465089-f2843980-4d7b-11eb-8caa-104c3e70343a.JPG)

### Screen Adjustment (스텝 모터)
![모터](https://user-images.githubusercontent.com/20160685/103464970-50645180-4d7b-11eb-9484-88069401e48f.JPG)

### UI Diversification
![UI버전](https://user-images.githubusercontent.com/20160685/103465170-aab1e200-4d7c-11eb-8c33-5f51f0a9ce15.JPG)

