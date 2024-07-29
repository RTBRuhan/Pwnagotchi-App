# Pwnagotchi App

Pwnagotchi is an Android app designed to interact with your Pwnagotchi device, providing easy access to its web interface and functionalities. The app includes features for Bluetooth tethering and convenient UI tweaks.

## Features

- **Web Interface Access:** Load and interact with the Pwnagotchi web interface directly from the app.
- **Bluetooth Tethering:** Enable Bluetooth tethering to connect your Pwnagotchi device seamlessly.
- **Full-Screen Mode:** Utilize the entire screen, including areas around the notch.
- **UI Tweaks:** Customize screen orientation and toggle full-screen mode.

## Getting Started

### Prerequisites

- Android device running Android 5.0 (Lollipop) or higher.
- Pwnagotchi device with a web interface accessible at a known URL.

### Installation

1. **Download the Latest Build:**
   - Go to the [Releases](https://github.com/RTBRuhan/pwnagotchi-app/releases) page and download the latest APK.
   - Install the APK on your Android device.

2. **Build from Source:**
   - Clone the repository:
     ```sh
     git clone https://github.com/RTBRuhan/pwnagotchi-app.git
     ```
   - Open the project in Android Studio.
   - Build and run the app on your Android device.

## Usage

### Initial Setup

1. On first launch, the app will request necessary permissions.
2. The app defaults to a web interface URL (`http://192.168.44.44:8080`). You can change this URL in the settings menu. 

### Main Features

- **Load Web Interface:**
  - The app loads the Pwnagotchi web interface on launch. 
  - You can change the URL by tapping the floating action button (FAB) and selecting "Change URL".

- **Enable Bluetooth Tethering:**
  - The app attempts to enable Bluetooth tethering automatically.
  - If required, the app will guide you to the settings to enable tethering manually.

- **UI Tweaks:**
  - Tap the FAB and select "Tweaks" to adjust screen orientation and toggle full-screen mode.

### Authentication

If the Pwnagotchi web interface requires authentication, the app will prompt you for a username and password, similar to how it is done in Chrome.

## Contributing

Contributions are welcome! Please fork the repository and submit pull requests.

## License

This project is licensed under the MIT License.

## Contact

For any questions or suggestions, please open an issue or contact me at [RTBRuhan](https://github.com/RTBRuhan).

---
