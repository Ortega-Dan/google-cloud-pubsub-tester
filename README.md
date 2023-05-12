Get your PubSub emulator running locally with:
```bash
docker run --rm -it -p 8085:8085 google/cloud-sdk:emulators gcloud beta emulators pubsub start --host-port=0.0.0.0:8085
```