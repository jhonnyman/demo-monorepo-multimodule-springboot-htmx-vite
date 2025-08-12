# Create root folder under `/opt`
sudo mkdir /opt/demo-com

# Create folders for Grafana, alloy, Minio
sudo mkdir /opt/demo-com/grafana /opt/demo-com/alloy /opt/demo-com/minio /opt/demo-com/loki /opt/demo-com/spring /opt/demo-com/keycloak /opt/demo-com/mailpit

# Grant ownership to Docker
sudo chown -R :docker /opt/demo-com

# Grand permissions to loki
sudo chown -R 10001:docker /opt/demo-com/loki
