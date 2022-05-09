import random
import select
import datetime
import zlib
import time
import threading
import collections
import socket

serverSocket = socket.socket(socket.AF_INET, socket.SOCK_DGRAM)
serverSocket.bind(('127.0.0.1', 1300))
address = ('127.0.0.1', 1300)
socket.setdefaulttimeout(5)


syn_ack = '100'
syn_ack = '100-OK'
syn_ack_packet_s = ',' + '\n' + syn_ack
synpacket, address = serverSocket.recvfrom(1024)
syn_from_client = synpacket.decode('utf-8')
checksum_value_syn = syn_from_client.split(',')[0]
packet_syn = syn_from_client.splitlines()
payload_syn= packet_syn[1]

syn_packet_c= ',' + '\n' + payload_syn
checksum_recalc_syn = zlib.crc32(str(syn_packet_c).encode('utf-8'))
checksumtwh1 = str(checksum_recalc_syn)

if(checksumtwh1 == checksum_value_syn): 
    checksum_twh_s = zlib.crc32(syn_ack_packet_s.encode('utf-8'))
    checksumtwh_s = str(checksum_twh_s)
    synack_to_client = checksumtwh_s + ',' + '\n' + syn_ack
    syn_ack_packet = synack_to_client.encode('utf-8')
    serverSocket.sendto(syn_ack_packet, address)

else:
    print('CHECKSUM DOES NOT MATCH, CONNECTION CANNOT BE ESTABLISHED')
    #timeout resend 


ackpacket1, address = serverSocket.recvfrom(1024)
ack_twh = ackpacket1.decode('utf-8')
checksum_ack_twh = ack_twh.split(',')[0]
packet_ack_twh = ack_twh.splitlines()
ack_twh1 = packet_ack_twh[1]

ack_from_client_syn_ack = ',' + '\n' + ack_twh1   
checksum_recalc_ack_s = zlib.crc32(str(ack_from_client_syn_ack).encode('utf-8'))
checksum_ack_1 = str(checksum_recalc_ack_s)


if(checksum_ack_1 == checksum_ack_twh):
    #print('ACKNOWLEDGMENT FROM CLIENT TO ESTABLISH CONNECTION RECEIVED')
    print('ACKNOWLEDGMENT FROM SERVER CONNECTION ESTABLISHED')

else:
    print('checksum doesnt match')
    #timeout resend 

print("SERVER CONNECTED")



#variables:
timeout_count = 0
seqCount = 0
client_count = 0
checksum_b = False
acknow_for_c = 0
input = ''
american_menu = 'Starter: Mini Burgers / Main: Buffalo Chicken / Dessert: Apple Pie / Drink: Coca-Cola'
french_menu = 'Starter: Crossiant / Main: Bœuf bourguignon / Dessert: Cake / Drink: Fanta'
medi_menu = 'Starter: Baba Ganoush / Main: Spanakopita / Dessert: "Baklava" / Drink: Iced Tea'
command_menu_today = "TODAY"
command_menus = "MENUS"
command_menu_american = "AMERICAN"
command_menu_medi = "MEDITERRANEAN"
command_menu_french = "FRENCH"
error_input = "INPUT NOT UNDERSTOOD / INPUT MUST INCLUDE ONE OF THE FOLLOWING COMMANDS:" + command_menu_american + ' ' + command_menu_medi + ' ' +  command_menu_french
dayOfTheWeek = datetime.datetime.today().weekday()
message1 = ''


client_count = 0
acknowledgment_for_client = ''


while (timeout_count<5):
    try:
        data, address = serverSocket.recvfrom(1024)
        data = data.decode('utf-8')
        timeout_count = 0
        checksum = data.split(',')[0]
        packetclient = data.splitlines()

        start = time.time()* 1000
        rand = random.randint(0, 10) 

        if(client_count==0):
            cs_optional1 = data.split(';')[0]
            optional = cs_optional1.split(',')[1]
            seqNumber_from_client = optional
            acknowledgment_for_client = seqNumber_from_client
            payload = packetclient[1]
            packet_form = ',' + optional + ';' + '\n' + payload



        elif(client_count>0):
            cs_optional1 = data.split(';')[0]
            optional_client = cs_optional1.split(',')[1]
            print(optional_client)
            ack_from_client = optional_client.split(':')[0]
            seqNumber_from_client = optional_client.split(':')[1]
            payload = packetclient[1]
            packet_form = ',' + optional_client + ';' + '\n' + payload
            acknowledgment_for_client = seqNumber_from_client
            print('Acknowledgment from client for packet number: ' + str(ack_from_client))

        client_input = payload.upper()   
        checksum_received = checksum
        messageFromClient = payload
        packet_rv = packet_form.encode('utf-8')
        checksum_on_data = zlib.crc32(packet_rv)
        c2 = str(checksum_on_data) 
        words = client_input.split()
        if(c2 == checksum_received):
            print('PACKET VALID')
            if command_menu_today in words:
                if dayOfTheWeek == 0:
                    message1 = 'Monday Menu: Starter: Salad / Main: Pasta / Dessert: Waffle / Drink: Fanta'
                    
                elif dayOfTheWeek == 1:
                    message1 = 'Tuesday Menu: Starter: Spring Rolls / Main: Rice / Dessert: Cake / Drink: Coca-Cola'
                    
                elif dayOfTheWeek == 2:
                    message1 = 'Wednesday Menu: Starter: Tuna Salad / Main: Chicken Wrap / Dessert: Icecream / Drink: Lipton Ice Tea'
                    
                elif dayOfTheWeek == 3:
                    message1 = 'Thursday Menu: Starter: Dough Balls / Main: Pizza / Dessert: Waffle / Drink: Fanta ' 
                    
                elif dayOfTheWeek == 4:
                    message1 = 'Friday Menu: Starter: Salad / Main: Lasagne / Dessert: Icecrean / Drink: Tango' 

                elif dayOfTheWeek == 5:
                    message1 = 'Saturday Menu: Starter: Dough Balls / Main: Rice / Dessert: Cookie Dough / Drink: Coca-Cola' 
                
                elif dayOfTheWeek == 6:
                    message1 = 'Sunday Menu: Starter: Spring Rolls / Main: Pasta / Dessert: Cake / Drink: Lipton Ice Tea' 

            elif command_menus in words:
                message1 = 'ALL MENUS ARE THE FOLLOWING: MENU TODAY / MENU MEDITERRANEAN / MENU FRENCH AND MENU AMERICAN / PLEASE SPECIFY ACCORDINGLY'
                
            elif command_menu_american in words:
                message1 = american_menu
                
            elif command_menu_medi in words:
                message1 = medi_menu

            elif command_menu_french in words:
                message1 = french_menu
                
            else: 
                message1 = error_input
            
        else:
             print('CHECKSUM DOES NOT MATCH, CORRUPTED PACKET RECEIVED')

        if rand < 2:
            print(str(message1)+" timing out")
        else:
            optional_server = str(acknowledgment_for_client) + ':' + str(seqCount)
            print("SENDING MENU: "+ message1)
            packet_format = ',' + optional_server + ';' + '\n'+ message1
            packet_to_c = packet_format.encode('utf-8') 
            checksum_value = zlib.crc32(packet_to_c)
            packet_to_client = str(checksum_value) + packet_format
            final_packet_to_send = packet_to_client.encode('utf-8')
        if(len(final_packet_to_send)>576):
                print('PACKET TOO LARGE')
        else:
                serverSocket.sendto(final_packet_to_send, address)
                client_count = client_count + 1
                seqCount = seqCount + 1
                print('REQUEST SENT')
                print(packet_to_client)
                
            
    except socket.timeout as inst:
        print('REQUEST TIMING OUT, RETRANSMISSION OCCURRING')
        timeout_count = timeout_count + 1