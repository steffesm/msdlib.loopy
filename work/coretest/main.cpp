#include "api/setup.h"
#include "api/components.h"

#include <bitset>
#include <iostream>

void led_test() {
  std::cout << "starting loopy gpio example led test" << std::endl;
  std::cout << "watch your board's leds carefully (;" << std::endl;

  // run gpo test
  gpio_leds.test();
  gpio_leds.test();
  gpio_leds.test();

  // reset led state to 0
  gpio_leds.writeState(std::bitset<8>(0));

  std::cout << "test has been completed" << std::endl;
}

void randomTest() {
  // init test seeds
  int reset = 0x00000001;
  int seed1 = 0x32408702;
  int seed2 = 0x39480457;
  int seed3 = 0x37543452;

  // init seed vector
  std::vector<std::bitset<32> > seeds;
  seeds.push_back(reset);
  seeds.push_back(seed1);
  seeds.push_back(seed2);
  seeds.push_back(seed3);
  seeds.push_back(seed1);
  seeds.push_back(seed1);
  seeds.push_back(seed1);

  // write seed vector
  rng_a.s_axis.write(seeds);

  // read values
  std::vector<std::bitset<32> > vals(8);
  rng_a.m_axis.read(vals);

  // print values
  for(unsigned int i = 0; i < vals.size(); i++)
      std::cout << std::endl << "value: " << vals[i].to_ulong();
}

void gpi_test() {
  std::cout << "starting loopy gpio example input test" << std::endl;
  std::cout << "press button 1 to increment the led state" << std::endl;
  std::cout << "press button 2 to reset the led state" << std::endl;
  std::cout << "press button 3 to print the count of these test executed so far" << std::endl;
  std::cout << "press button 4 to lock client until it is pressed again" << std::endl;
  std::cout << "press button 5 to terminate example application" << std::endl;
  std::cout << "press several buttons at once for a suprise" << std::endl;

  bool var = true;
  int ledState = 0;
  int count = 0;
  while(var) {
    gpio_buttons.waitForChange();

    std::bitset<5> state = gpio_buttons.readState();

    switch(state.to_ulong()) {
    case 0: break; // changing the state back to 0 also triggers an event
    case 1: count++;
    std::cout << "you pressed button 1!" << std::endl;
      ledState++;
      ledState = ledState % 256;
      gpio_leds.writeState(std::bitset<8>(ledState));
      break;
    case 2: count++;
    std::cout << "you pressed button 2!" << std::endl;
      gpio_leds.writeState(std::bitset<8>(0));
      break;
    case 4: count++;
    std::cout << "you pressed button 3!" << std::endl;
    std::cout << "in total, you've executed " << count << " button examples so far" << std::endl;
      break;
    case 8: count++;
      std::cout << "you pressed button 4!" << std::endl;
      do {
    	  gpio_buttons.waitForChange();
      } while(gpio_buttons.readState() != 8);
      std::cout << "you pressed button 4 again!" << std::endl;
      break;
    case 16: count++;
    std::cout << "you pressed button 5! terminating" << std::endl;
      var = false;
      break;
    default: count++;
    std::cout << "you pressed multiple buttons at once!" << std::endl;
      gpio_leds.writeState(std::bitset<8>("10101010"));
      sleep(1);
      gpio_leds.writeState(std::bitset<8>("01010101"));
      sleep(1);
      gpio_leds.writeState(std::bitset<8>("10101010"));
      sleep(1);
      gpio_leds.writeState(std::bitset<8>("01010101"));
      sleep(1);
      gpio_leds.writeState(std::bitset<8>(0));
      break;
    }
  }
}

int main() {
  startup("131.246.74.2");
  led_test();
  
  randomTest();
  
  gpi_test();

  shutdown();
}
