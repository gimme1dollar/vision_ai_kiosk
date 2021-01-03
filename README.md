# Viosk
폭 넓은 사용층을 고려한 AI-기반의 **비전인식 키오스크** 
- [소개 영상](https://drive.google.com/file/d/1KaQiv4moTRgdAZZi3DC1T_f7XEgCQWQI/view?usp=sharing) 
    - 이미지 출처: `files/_image_reference.txt`
- [시연 영상](https://drive.google.com/file/d/1vzOOIthxaEInF-no1CoXh7OKscDMb2IW/view?usp=sharing)
- [발표 자료](https://drive.google.com/file/d/1hJ28Oalcn_aavSERfy1kXunzW1Yjd5MP/view?usp=sharing)

## 개요
### Goal
1. 코로나-19 시대 이후 비접촉 무인 시스템의 활성화로 인해 더 많아진 터치스크린 기반 키오스크의
2. 사용을 어려워하는 글자를 읽는 데에 어려움이 있으시는 분, 높이가 맞지 않아 터치가 힘드신 분, 휠체어를 타시는 분들을 위해
3. 비전 인식 AI를 활용해 더 나은 사용 환경을 제공하는 키오스크

### Overall Structure
<p align="center">
	<img src="https://user-images.githubusercontent.com/20160685/103464856-5279e080-4d7a-11eb-96b9-679e7fc523df.JPG" height="256px"/>
	<img src="https://user-images.githubusercontent.com/20160685/103474375-8854ae80-4de6-11eb-84bb-dd130c3a2df6.jpeg" height="256px"/>
	<img src="https://user-images.githubusercontent.com/20160685/103464992-6eca4d00-4d7b-11eb-9632-8b5e38db922c.JPG" height="256px"/>
</p>

## Development
### Environment
#### STM32
- STM-B-L4S5I-IOT01A : Board
- STM32CubeMX : Development Tool
- STM32CubeAI : AI Library

#### Hardware 
- Fusion360 : 3D modeling
- Galaxy Tab : mini-screen Kiosk

#### Firmware
- Rasberry Pi : 사진 촬영 및 사진 전송 (UART)

#### Software
- Keras (ver.2.4.0) : Convolutional Neural Network for Vision Recognition
- Kotlin : Android Application Development

### Features
#### Age Detection
- [AgeGenderDeepLearning](https://github.com/GilLevi/AgeGenderDeepLearning) 모델을 base로 진행
- [IMDB-Wiki](https://data.vision.ee.ethz.ch/cvl/rrothe/imdb-wiki/) 로 학습

##### Normalize, class grouping, gray scale 변환 등의 preprocessing 진행
<p align="center">
<img src="https://user-images.githubusercontent.com/20160685/103465050-94efed00-4d7b-11eb-9887-f54f2f342dcb.JPG" height="256px"/>
<img src="https://user-images.githubusercontent.com/20160685/103474402-ceaa0d80-4de6-11eb-95b5-68cf5102c133.jpeg" height="256px"/>
</p>

##### Training 결과, 90%의 정확도를 보임
<p align="center">
<img src="https://user-images.githubusercontent.com/20160685/103465046-8efa0c00-4d7b-11eb-88bf-2392bb91d23f.JPG" height="256px"/>
</p>

##### Parameter 축소, quantization 등을 통해 여전히 높은 정확도를 보이는 lightweight 모델 생성 및 STM32 보드에 탑재
<p align="center">
<img src="https://user-images.githubusercontent.com/20160685/103465267-43e0f880-4d7d-11eb-850c-6569bd7df9bc.JPG" height="256px"/>
<img src="https://user-images.githubusercontent.com/20160685/103465252-2449d000-4d7d-11eb-9c24-b19ad9feac3f.JPG" height="256px"/>
</p>

#### UI
##### 초음파 센서를 이용해 키를 인지하고, 그에 맞춰 키오스크 높이를 조정
<p align="center">
<img src="https://user-images.githubusercontent.com/20160685/103465089-f2843980-4d7b-11eb-8caa-104c3e70343a.JPG" height="256px"/>
<img src="https://user-images.githubusercontent.com/20160685/103464970-50645180-4d7b-11eb-9484-88069401e48f.JPG" height="256px"/>
<img src="https://user-images.githubusercontent.com/20160685/103474440-2fd1e100-4de7-11eb-913b-8de8fcef6ce0.JPG" height="256px"/>
</p>

##### Age detection 결과에 맞춰, UI를 변경
<p align="center">
    <img src="https://user-images.githubusercontent.com/20160685/103465170-aab1e200-4d7c-11eb-8c33-5f51f0a9ce15.JPG" height="256px"/>
</p>

## Contributers
<p align="center">
    <img src="https://user-images.githubusercontent.com/20160685/103464867-5f96cf80-4d7a-11eb-9195-0ad0df3b8d86.JPG" width="50%"/>
</p>

- 김현성 (https://github.com/hyunsungkim).   
- 김주환 (https://github.com/robotjuhwan).   
- 이주용 (https://github.com/gimme1dollar).   
- 조승혁 (https://github.com/seunghyukcho).   
