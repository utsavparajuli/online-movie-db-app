# 2023-fall-cs122b-team-night

- # General
    - #### Team#: Team-Night
    
    - #### Names: Daniel Bremner, Utsav Parajuli
    
    - #### Project 5 Video Demo Link:

    - #### Instruction of deployment:
      - Git Clone the repo, cd into the directory and run 'mvn package'.
      - Go to http://localhost:8080/manager/html/, load the package, and click the link provided

    - #### Collaborations and Work Distribution:
      - Daniel Bremner:
      - Utsave Parajuli: 


- # Connection Pooling
    - #### Include the filename/path of all code/configuration files in GitHub of using JDBC Connection Pooling.
      - Webcontent/META-INF/context.xml
      - Webcontent/WEB-INF/web.xml
      - src/*.java
    
    - #### Explain how Connection Pooling is utilized in the Fabflix code.
      - Connections are defined in the context.xml file and called in each servlet in the src folder
    
    - #### Explain how Connection Pooling works with two backend SQL.
      - In context.xml we allow the creation of a maximum of 100 cached connections for each defined connection.
      - In any writing calls we use a connection that points to the primary SQL server.
      - In any reading calls we use a connection that points to the primary/secondary SQL server.
      - Whenever a servlet is called instead of opening/closing a new connection one of the cached connections is used.
    

- # Master/Slave
    - #### Include the filename/path of all code/configuration files in GitHub of routing queries to Primary/Secondary SQL.
      - Webcontent/META-INF/context.xml
      - src/*.java

    - #### How read/write requests were routed to Primary/Secondary SQL?
      - We have two connections defined, one that points to the primary SQL server, and one that points to the secondary SQL server.
      - For any writing requests we use the connection that points to the primary server.
      - For any reading requests we use either a conneciton that points to the primary or secondary server.
    

- # JMeter TS/TJ Time Logs
    - #### Instructions of how to use the `log_processing.*` script to process the JMeter logs.
      - 


- # JMeter TS/TJ Time Measurement Report

| **Single-instance Version Test Plan**          | **Graph Results Screenshot** | **Average Query Time(ms)** | **Average Search Servlet Time(ms)** | **Average JDBC Time(ms)** | **Analysis** |
|------------------------------------------------|------------------------------|----------------------------|-------------------------------------|---------------------------|--------------|
| Case 1: HTTP/1 thread                          | ![/src/img/single_case1](path to image in img/)   | 132                        | 57                                  | 114                       | ??           |
| Case 2: HTTP/10 threads                        | ![/src/img/single_case2](path to image in img/)   | 388                        | 200                                 | 369                       | ??           |
| Case 3: HTTPS/10 threads                       | ![/src/img/single_case3](path to image in img/)   | 395                        | 283                                 | 375                       | ??           |
| Case 4: HTTP/10 threads/No connection pooling  | ![/src/img/single_case4](path to image in img/)   | 428                        | 281                                 | 410                       | ??           |


                                                                                                                            TJ                                TS
| **Scaled Version Test Plan**                   | **Graph Results Screenshot** | **Average Query Time(ms)** | **Average Search Servlet Time(ms) ** | **Average JDBC Time(ms)** | **Analysis** |
|------------------------------------------------|------------------------------|----------------------------|--------------------------------------|---------------------------|--------------|
| Case 1: HTTP/1 thread                          | ![/src/img/scaled_case1](path to image in img/)   | 141                        | 57                                   | 122                       | ??           |
| Case 2: HTTP/10 threads                        | ![/src/img/scaled_case2](path to image in img/)   | 527                        | 379                                  | 508                       | ??           |
| Case 3: HTTP/10 threads/No connection pooling  | ![/src/img/scaled_case3](path to image in img/)   | 472                        | 315                                  | 453                       | ??           |
