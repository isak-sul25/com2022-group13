import sys
import socket
import time
import random
import datetime
import collections
import zlib

UDP_IP_ADDRESS = "127.0.0.1"
UDP_PORT_NO = 1300
address = ("127.0.0.1",1300)
socket.setdefaulttimeout(5)
clientSocket= socket.socket(socket.AF_INET, socket.SOCK_DGRAM)






syn_client = '100'
ack_server = 'OK'
cache_list = []
cache_1 = ''
state_cache = False

syn_packet_form1 = ',' + '\n' + syn_client
checksum_syn_packet = zlib.crc32(syn_packet_form1.encode('utf-8'))
syn_packet = str(checksum_syn_packet) + ',' + '\n' + str(syn_client)
syn_packet_send= syn_packet.encode('utf-8') 
syn_packet_client = clientSocket.sendto(syn_packet_send,address)  



syn_ack, server = clientSocket.recvfrom(1024)
packet_synack = syn_ack.decode('utf-8')
checksum_from_server_syn= packet_synack.split(',')[0]
packet_synack1 = packet_synack.splitlines()
syn_ack_server= packet_synack1[1]

syn_packet_form2 = ',' + '\n' + syn_ack_server
checksum_synack = zlib.crc32(syn_packet_form2.encode('utf-8'))
checksum_synack1 = str(checksum_synack)

if(checksum_synack1 == checksum_from_server_syn):
    syn_packet_form3 = ',' + '\n' + ack_server
    syn_packet_form3 = syn_packet_form3.encode('utf-8')
    checksum_ack_s = zlib.crc32(syn_packet_form3)
    checksum_ack_s1 = str(checksum_ack_s)
    ack_to_client_twh = checksum_ack_s1 + ',' + '\n' + 'OK'
    ack_packet_twh = ack_to_client_twh.encode('utf-8')
    rtt_time=time.time() *1000
    ack_twh = clientSocket.sendto(ack_packet_twh,address)
    print('ACKNOWLEDGMENT FROM CLIENT CONNECTION ESTABLISHED')

else:
    print('CHECKSUM DOES NOT MATCH, CONNECTION CANNOT BE ESTABLISHED')
    #timeout resend 

print("CLIENT CONNECTED")





seqNum = 0 
rreturn = ''
ack_for_s = ''
messageToServer = ''
print("ENTER MENUS TO VIEW ALL AVAILABLE MENUS:" + '\n' + "ENTER TODAY TO VIEW THE MENU OF THE DAY:" + '\n' + "ENTER MENU <MENU NAME> TO VIEW MENU <NAME>:" + '\n' + "ENTER EXIT TO QUIT USER INPUT:")
while messageToServer!='EXIT': 
    try:



        rand = random.randint(0, 10)
        start = time.time()* 1000
        messageToServer = input("PLEASE REQUEST A MENU: ")

        if(messageToServer == 'EXIT'):
            print('GOODBYE')
            clientSocket.close()
            break

        for x in cache_list:
            response1 = x.split(',')[0]
            if response1 == messageToServer:
                rreturn = x.split(',')[1]
                print('MENU PREVIOUSLY REQUESTED')
                print(rreturn)
                cache_1 = 'COMPLETE'
        if(cache_1 == 'COMPLETE'):
            continue


        if rand < 2:
                print(str(messageToServer)+" timing out")
        else:
            if(seqNum==0):
                optional = str(seqNum)
           
            elif(seqNum>0):
                optional = str(ack_for_s) + ':' + str(seqNum)
   
            packet_format = ',' + optional + ';' + '\n' + messageToServer
            packet_to_s = packet_format.encode('utf-8') 
            checksum_value = zlib.crc32(packet_to_s)
            packet_to_server = str(checksum_value) + ',' + optional + ';' + '\n' + messageToServer
            packet_to_server1 = packet_to_server.encode('utf-8') 
            if(len(packet_to_server1)>576):
                print('PACKET TOO LARGE')
            else:
                sent = clientSocket.sendto(packet_to_server1,address)
            


        data, server = clientSocket.recvfrom(1024)
        packet = data.decode('utf-8')
        packetfromserver = packet.splitlines()
        checksum_from_s= packet.split(',')[0]
        cs_optional = packet.split(';')[0]
        optional1 = cs_optional.split(',')[1]
        payload = packetfromserver[1]
        ack_from_server = optional1.split(':')[0]
        seq_from_s = optional1.split(':')[1]

        ack_for_s = seq_from_s
        

        packet_receiv_format = ',' + optional1 + ';' + '\n' + payload
        packet_rcv1 = packet_receiv_format.encode('utf-8')
        recalculate_checksum_server = zlib.crc32(packet_rcv1)
        
     
        if(checksum_from_s == str(recalculate_checksum_server)):
            print('PACKET VALID')
            print('Acknowledgment from server for packet number: ' + str(ack_from_server))
            print("The menu is as follows:")
            print(payload)
            print ('RECEIVED after {} ms'.format((time.time() *1000)-rtt_time))
            seqNum  = seqNum + 1  
            cache_list.append(messageToServer + ',' + payload)                            
        else:
            print('CHECKSUM DOES NOT MATCH, CORRUPTED PACKET RECEIVED')

          
    except socket.timeout as inst:
        print('REQUEST TIMING OUT, RETRANSMISSION OCCURRING')
    