
#include "MPU6050_Constants.h"
#include "Changeable_Settings.h"

#include<Wire.h>

const int MPU_addr = 0x68; // I2C address of the MPU-6050
int16_t AcX, AcY, AcZ, GyX, GyY, GyZ;

float expFilterAlpha[3] = {largeAlpha, mediumAlpha, smallAlpha};


float mediumExponentialFilterX;
float mediumExponentialFilterZ;
float mediumExponentialFilterY;
float mediumExponentialFilterGyX;
float mediumExponentialFilterGyY;
float mediumExponentialFilterGyZ;



float prevAccX = 0;
unsigned long prevTime = millis();
unsigned long currTime;




void setup() {
  Serial.begin(9600);
  
  
  Wire.begin();
  Wire.beginTransmission(MPU_addr);
  Wire.write(0x6B); // PWR_MGMT_1 register
  Wire.write(0); // set to zero (wakes up the MPU-6050)
  Wire.endTransmission(true);

  //Gyro-sensitivity
  Wire.beginTransmission(MPU_addr);
  Wire.write(0x1B);
  Wire.write(0b00011000);
  Wire.endTransmission(true);

  //Accel-sensitivity
  Wire.beginTransmission(MPU_addr);
  Wire.write(0x1C);
  Wire.write(0b00010000);
  Wire.endTransmission(true);

}

void loop() {

  //Reading x-acceleration
  Wire.beginTransmission(MPU_addr);
  Wire.write(0x3B); // starting with register 0x3B (ACCEL_XOUT_H)
  Wire.endTransmission(false);
  Wire.requestFrom(MPU_addr, 2, true); // request a total of 2 registers
  AcX = Wire.read() << 8 | Wire.read(); // 0x3B (ACCEL_XOUT_H) & 0x3C (ACCEL_XOUT_L)

  //Reading y-acceleration
  Wire.beginTransmission(MPU_addr);
  Wire.write(0x3D); // starting with register 0x3B (ACCEL_XOUT_H)
  Wire.endTransmission(false);
  Wire.requestFrom(MPU_addr, 2, true); // request a total of 2 registers
  AcY = Wire.read() << 8 | Wire.read(); // 0x3B (ACCEL_XOUT_H) & 0x3C (ACCEL_XOUT_L)

  //Reading z-acceleration
  Wire.beginTransmission(MPU_addr);
  Wire.write(0x3F); // starting with register 0x3F (ACCEL_ZOUT_H)
  Wire.endTransmission(false);
  Wire.requestFrom(MPU_addr, 2, true); // request a total 2 registers
  AcZ = Wire.read() << 8 | Wire.read(); // 0x3F (ACCEL_ZOUT_H) & 0x40 (ACCEL_ZOUT_L)

  //Reading x-gyroscope
  Wire.beginTransmission(MPU_addr);
  Wire.write(0x43); // starting with register 0x45 (GYRO_YOUT_L)
  Wire.endTransmission(false);
  Wire.requestFrom(MPU_addr, 2, true); // request a total of 2 registers
  GyX = Wire.read() << 8 | Wire.read(); // 0x45 (GYRO_YOUT_H) & 0x46 (GYRO_YOUT_L)

  //Reading y-gyroscope
  Wire.beginTransmission(MPU_addr);
  Wire.write(0x45); // starting with register 0x45 (GYRO_YOUT_L)
  Wire.endTransmission(false);
  Wire.requestFrom(MPU_addr, 2, true); // request a total of 2 registers
  GyY = Wire.read() << 8 | Wire.read(); // 0x45 (GYRO_YOUT_H) & 0x46 (GYRO_YOUT_L)
  
  //Reading z-gyroscope
  Wire.beginTransmission(MPU_addr);
  Wire.write(0x47); // starting with register 0x45 (GYRO_YOUT_L)
  Wire.endTransmission(false);
  Wire.requestFrom(MPU_addr, 2, true); // request a total of 2 registers
  GyZ = Wire.read() << 8 | Wire.read(); // 0x45 (GYRO_YOUT_H) & 0x46 (GYRO_YOUT_L)
  

  static float expMediumX = AcX;//Needed for the first time, because of the implementation as function.
  static float expMediumY = AcY;
  static float expMediumZ = AcZ;  //Otherwise the Angles would be adulterated at the first measurements
  static float expMediumGyX = GyX;
  static float expMediumGyY = GyY;
  static float expMediumGyZ = GyZ;
  
  //medium Alpha means medium Exponential Filter effect
  expMediumX = expFilterAlpha[1] * AcX + (1 - expFilterAlpha[1]) * expMediumX;
  mediumExponentialFilterX = expMediumX ;

   //medium Alpha means medium Exponential Filter effect
  expMediumY = expFilterAlpha[1] * AcY + (1 - expFilterAlpha[1]) * expMediumY;
  mediumExponentialFilterY = expMediumY ;

  //medium Alpha means medium Exponential Filter effect
  expMediumZ = expFilterAlpha[1] * AcZ + (1 - expFilterAlpha[1]) * expMediumZ;
  mediumExponentialFilterZ = expMediumZ ;

  //medium Alpha means medium Exponential Filter effect
  expMediumGyX = expFilterAlpha[1] * GyX + (1 - expFilterAlpha[1]) * expMediumGyX;
  mediumExponentialFilterGyX = expMediumGyX ;

  //medium Alpha means medium Exponential Filter effect
  expMediumGyY = expFilterAlpha[1] * GyY + (1 - expFilterAlpha[1]) * expMediumGyY;
  mediumExponentialFilterGyY = expMediumGyY ;

  //medium Alpha means medium Exponential Filter effect
  expMediumGyZ = expFilterAlpha[1] * GyZ + (1 - expFilterAlpha[1]) * expMediumGyZ;
  mediumExponentialFilterGyZ = expMediumGyZ ;

  

   
  
  Serial.print(mediumExponentialFilterX);
  Serial.print(" ");
  Serial.print(mediumExponentialFilterY);
  Serial.print(" ");
  Serial.print(mediumExponentialFilterZ);
  Serial.print(" ");
  Serial.print(mediumExponentialFilterGyX);
  Serial.print(" ");
  Serial.print(mediumExponentialFilterGyY);
  Serial.print(" ");
  Serial.println(mediumExponentialFilterGyZ);
  
  
  

  

  //Printing variables for serial monitor and serial plotter
  /*Serial.print(mediumExponentialFilterX);
  Serial.print("\t");
  Serial.print(mediumExponentialFilterZ);
  Serial.print("\t");
  Serial.println(mediumExponentialFilterGY);*/

}
