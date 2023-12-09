# log_processing.py

ts_values = []
tj_values = []

# Process TS file
with open('tsMeasurement.txt', 'r') as ts_file:
    for line in ts_file:
        if 'TS:' in line:
            ts_values.append(int(line.split(': ')[1].split(' ')[0]))

# Process TJ file
with open('tjMeasurement.txt', 'r') as tj_file:
    for line in tj_file:
        if 'TJ:' in line:
            tj_values.append(int(line.split(': ')[1].split(' ')[0]))

# Calculate averages
average_ts = sum(ts_values) / len(ts_values) if len(ts_values) > 0 else 0  # Avoid division by zero
average_tj = sum(tj_values) / len(tj_values) if len(tj_values) > 0 else 0

print(f'Average TS: {average_ts/1000000} ms')
print(f'Average TJ: {average_tj/1000000} ms')