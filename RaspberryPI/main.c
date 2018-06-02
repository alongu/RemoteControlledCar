#include <bcm2835.h>
#include <stdio.h>
//#include <iostream>
//include <cstring>      // Needed for memset
#include <sys/socket.h> // Needed for the socket functions
#include <netdb.h>      // Needed for the socket functions
//#include <sys/types.h> 
//#include <string.h>
#include <stdlib.h>
//#include <unistd.h>
//#include <errno.h>
//#include <netinet/in.h>
//#include <sys/un.h>
//#include <arpa/inet.h>

#define ENGINE_PIN RPI_GPIO_P1_12
#define SERVO_PIN RPI_V2_GPIO_P1_35

#define ENGINE_PWM_CHANNEL 0
#define SERVO_PWM_CHANNEL 1

#define ENGINE_RANGE 6000
#define SERVO_RANGE 6000

int status;
struct addrinfo host_info;       // The struct that getaddrinfo() fills up with data.
struct addrinfo *host_info_list = NULL; // Pointer to the to the linked list of host_info's.
int socketfd ; // The socket descripter
unsigned int RightLeft;
unsigned int UpDown;

int main(int argc, char **argv)
{
    if (!bcm2835_init())
        return 1;
	bcm2835_pwm_set_mode(ENGINE_PWM_CHANNEL, 1, 0);
	bcm2835_gpio_fsel(SERVO_PIN, BCM2835_GPIO_FSEL_ALT5);
	bcm2835_gpio_fsel(ENGINE_PIN, BCM2835_GPIO_FSEL_ALT5);
    
	// my calc -
	// clock divider of 64 -> 19200000 / 64 = 300000
	// Range for servo must be -> 6000 - to achive 50 Hz frequency (Channel 0)
	// Range for Engine -> 6000 - will achive 50 Hz frequency (Channel 1))
    bcm2835_pwm_set_clock(BCM2835_PWM_CLOCK_DIVIDER_64);
    bcm2835_pwm_set_mode(SERVO_PWM_CHANNEL, 1, 1);
	bcm2835_delay(100);
    bcm2835_pwm_set_range(SERVO_PWM_CHANNEL, SERVO_RANGE);
	
	bcm2835_pwm_set_mode(ENGINE_PWM_CHANNEL, 1, 1);
	bcm2835_delay(100);
    bcm2835_pwm_set_range(ENGINE_PWM_CHANNEL, ENGINE_RANGE);
	
	// Set the pin to be an output
    bcm2835_gpio_fsel(RPI_GPIO_P1_16, BCM2835_GPIO_FSEL_OUTP);
	bcm2835_gpio_fsel(RPI_GPIO_P1_18, BCM2835_GPIO_FSEL_OUTP);
	bcm2835_gpio_write(RPI_GPIO_P1_16, LOW);
	bcm2835_gpio_write(RPI_GPIO_P1_18, HIGH);
	
	// Set Servo to middle position //
	bcm2835_pwm_set_data(SERVO_PWM_CHANNEL, 450);
	bcm2835_delay(1000);
	
	host_info.ai_family = AF_UNSPEC;     // IP version not specified. Can be both.
    host_info.ai_socktype = SOCK_STREAM; // Use SOCK_STREAM for TCP or SOCK_DGRAM for UDP.
    host_info.ai_flags = AI_PASSIVE;     // IP Wildcard
	
	status = getaddrinfo("192.168.43.155", "9999", &host_info, &host_info_list);
	socketfd = socket(host_info_list->ai_family, host_info_list->ai_socktype,
                      host_info_list->ai_protocol);
	int yes = 1;
    status = setsockopt(socketfd, SOL_SOCKET, SO_REUSEADDR, &yes, sizeof(int));
    status = bind(socketfd, host_info_list->ai_addr, host_info_list->ai_addrlen);
	status =  listen(socketfd, 5);
	
	// Waiting for connection from someone //
	int new_sd;
    struct sockaddr_storage their_addr;
    socklen_t addr_size = sizeof(their_addr);
    new_sd = accept(socketfd, (struct sockaddr *)&their_addr, &addr_size);
	
	// from here on if new_sd is OK, we continue in loop until end //
	ssize_t bytes_recieved;
    char incomming_data_buffer[50];
	
	system("sudo service motion start"); // start motion 
	
	while (1)
	{
		bytes_recieved = recv(new_sd, incomming_data_buffer,1000, 0);
		
		if (incomming_data_buffer[0] == 'Q')
		{
			break;
		}
		
		if (incomming_data_buffer[0] == 'H')
		{
			// Servo Handling //
			RightLeft = (incomming_data_buffer[1] - '0') * 10 + (incomming_data_buffer[2] - '0');
			bcm2835_pwm_set_data(SERVO_PWM_CHANNEL, 600 - (3 * RightLeft)); // was 300 + instead of 600 -
			
			UpDown = ((incomming_data_buffer[4] - '0') * 10 + (incomming_data_buffer[5] - '0'))*120; // 50 MAX is 6000, while minimum is 0 (2%)
			if (incomming_data_buffer[3] == 'U')
			{
				bcm2835_gpio_write(RPI_GPIO_P1_16, HIGH);
				bcm2835_gpio_write(RPI_GPIO_P1_18, LOW);
			}
			else
			{
				bcm2835_gpio_write(RPI_GPIO_P1_16, LOW);
				bcm2835_gpio_write(RPI_GPIO_P1_18, HIGH);
			}
			
			bcm2835_pwm_set_data(ENGINE_PWM_CHANNEL, UpDown);
		}	
	}
	
	system("sudo service motion stop"); // stop motion
	
	bcm2835_pwm_set_mode(ENGINE_PWM_CHANNEL, 1, 0);
	bcm2835_delay(100);
	bcm2835_pwm_set_mode(SERVO_PWM_CHANNEL, 1, 0);
	bcm2835_delay(100);
	bcm2835_close();
    return 0;
    /*
	
	*/
	
	
	
	
	
}