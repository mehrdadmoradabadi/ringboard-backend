# RingBoard

<div align="center">

![RingBoard Logo](./src/main/resources/static/logo.webp)

A modern, enterprise-grade PBX Monitoring system powered by Spring Boot and Asterisk

[![License](https://img.shields.io/badge/license-MIT-blue.svg)](LICENSE)
[![Version](https://img.shields.io/badge/version-1.0.0-green.svg)](https://github.com/yourusername/ringboard/releases)


</div>

## 🎯 Overview

RingBoard is an open-source solution designed to simplify an enterprise telephony Monitoring system with seamless integration into Asterisk PBX. As a VoIP engineer with years of experience in call centers, I know how critical it is to have real-time insights into call processes—like total calls, abandoned calls, and other key reports on a single dashboard.

From my experience, there’s no free or open-source tool that provides these essential analytics for call centers. That’s why I built RingBoard—to give admins and call center managers a free and flexible alternative.

Right now, RingBoard is a REST API, but I’m working on a simple UI to make it even more accessible. It’s completely free to use, as long as you follow the license terms. 😃

### How You Can Help
- If RingBoard is useful for you, please ⭐ the project!
- Want to extend it? Fork the repo and contribute!
- Currently, RingBoard only supports Asterisk (since that’s what I have access to). If you can provide access or details for other PBX systems, feel free to create a branch and add support—or contact me!
## Contributing

I welcome contributions!
There is already a UI branch that I'm working on it for a simple UI,
but If you want to contribute, Please follow these steps:

1. Fork the repository
2. Create a feature branch (`git checkout -b feature/amazing-feature`)
3. Commit changes (`git commit -m 'Add amazing feature'`)
4. Push to branch (`git push origin feature/amazing-feature`)
5. Open a Pull Request

### Key Benefits

- 📊 **Real-time Dashboard**: Monitor PBX system status, call metrics, and performance indicators
- 🔐 **Enterprise Security**: Role-based access control and comprehensive audit logging
- 📚 **API-First Design**: Full-featured REST API with Swagger/OpenAPI documentation
- 🚀 **Scalability**: Handles enterprise-level deployments with ease
- 📈 **Advanced Monitoring**: Detailed logging, metrics, and health monitoring

##  Features
- **PBX Monitoring**
    - Extension configuration
    - Call routing rules
    - IVR Monitoring
    - Queue administration
- **PBX API**
    - REST API for managing PBX systems
    - Swagger/OpenAPI documentation
## Technology Stack

- **Backend**
  - Java 17
  - Spring Boot
  - Spring Security
  - Spring Data JPA
- **Database**
  - MySql
- **Integration**
  - Asterisk Java
  - REST APIs
  - WebSocket for real-time updates

## Quick Start

### Prerequisites

- JDK 17+
- MySql 8+
- Maven 3.8+
- Asterisk PBX Server

### Installation

1. **Clone the repository**

   ```bash
   git clone https://github.com/mehrdadmoradabadi/ringboard-backend.git
   cd ringboard-backend
   ```

2. **Configure the database**
   ```properties
   # src/main/resources/application.properties
   spring.datasource.url=jdbc:mysql://${SPRING_DATASOURCE_URL:localhost}:3306/${SPRING_DATASOURCE_DBNAME:ringboard}
   spring.datasource.username=your_username
   spring.datasource.password=your_password
   ```

3. **Build and run**
    This project does not require Docker to work.
   * if you want to run it on your local machine no docker, you can follow these steps:
       * Clone the repository
       * Run   
           ```bash 
             mvn clean install
            ````

       *  Create a MySQL database named `ringboard`
       * modify `src/main/resources/application.properties`
       *  Run   
            ```bash
            mvn spring-boot:run
            ```

   * But if you want to run it on Docker, you can follow these steps:
       * Clone the repository
       * modify the `docker-compose.yml` and application.properties
       * RUN the docker-compose file
       * Enjoy IT
   

4. **Access the application**
   - Swagger UI: `http://localhost:8080/swagger-ui.html`

### Project Structure

```
/
├── src/main/
│       ├── java/com/ringboard/
│       │   ├── pbx/
│       │   │   └── PBX files
│       │   ├── resources/
│       │   │   └── resources files
│       │   ├── security/
│       │   │   └── security configuration files
│       │   ├── ugroup/
│       │   │   └── ugroup files
│       │   ├── user/
│       │   │   └── user files
│       │   └── utils/
│       │       └── utils files
│       └── resources/
│           └── application.properties
├── pom.xml
├── mvnw
├── mvnw.cmd
├── README.md
└── LICENSE
```

### API Documentation

- **Swagger UI**: `http://localhost:8080/swagger-ui.html`
- **OpenAPI Spec**: `http://localhost:8080/v3/api-docs`


### Development Guidelines

- Follow Java code style guidelines
- Write unit tests for new features
- Update documentation as needed
- Add comments for complex logic

## 📄 License

This project is licensed under the MIT License — see the [LICENSE](./LICENSE) file for details.

## Support & Contact

- 📧 Email: mehrdadmoradabadi@gmail.com

- 📝 Issues: [GitHub Issues](https://github.com/mehrdadmoradabadi/ringboard-backend/issues)

---

<div align="center">
Made by Mehrdad Moradabadi
</div>
