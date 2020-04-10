import pandas as pd
import matplotlib.pyplot as plt
import argparse
import numpy as np


def read_columns(filename):
    with open(filename) as file:
        for line in file:
            if line[0] == '#':
                return line.split()[1:]


def get_line(filename, column):
    with open(filename) as file:
        for line in file:
            if line[0] != '#' or line[0] != " ":
                if len(line.split()[1:]) != 0:
                        # print(line.split()[1:])
                        yield line.split(maxsplit=column-1)


def plot(cpu, mem, io):

    time = range(0, len(io))
    fig, ax1 = plt.subplots()

    color = 'tab:red'
    ax1.set_xlabel('time (s)')
    ax1.set_ylabel('CPU (%)', color=color)
    ax1.plot(time, cpu, color=color, label="CPU")
    ax1.tick_params(axis='y', labelcolor=color)
    plt.xticks(np.arange(0, len(cpu), 50))
    ax1.legend(bbox_to_anchor=(0.062, 1.006))

    ax2 = ax1.twinx()

    color = 'tab:blue'
    ax2.set_ylabel('Memory (GB)', color=color)
    ax2.plot(time, mem, color=color, label="MEM")
    ax2.plot(time, io, color='tab:orange', label="I/O")
    ax2.tick_params(axis='y', labelcolor=color)

    plt.title("64GB - CPU, MEMORY, I/O")
    plt.legend()

    fig.tight_layout()
    plt.show()

    io = data["kB_rd/s"] / 1000 + data["kB_wr/s"] / 1000
    io = io[:-1]
    plt.title("I/O utilization MB/sec")
    plt.xlabel("Time (seconds)")
    plt.ylabel("I/O (MB/sec)")
    plt.xticks(np.arange(0, len(io), 50))
    plt.plot(time, io, label="I/O", )
    plt.legend()
    plt.show()


if __name__ == "__main__":

    parser = argparse.ArgumentParser()

    parser.add_argument('file', type=str, help="give data file")
    parser.add_argument('process_id', type=str, help="process id")

    args = parser.parse_args()

    print("\nPlotting from file: {} \nProcess id: {}".format(args.file, args.process_id))

    filename = args.file
    columns = read_columns(filename)

    # print(columns)

    data = pd.DataFrame.from_records(get_line(filename, len(columns)), columns=columns)

    # drop unwanted columns from data
    data = data.drop(columns=["Command", "iodelay", "Time", "UID", "%usr", "%guest", "%system",
                          "majflt/s", "%wait", "CPU", "minflt/s", "RSS", "VSZ", "kB_ccwr/s"])

    data.drop(data[data['PID'] != str(args.process_id)].index, inplace=True)

    # print(data)

    data['%CPU'] = data['%CPU'].astype(str).astype(float)
    data['%MEM'] = data['%MEM'].astype(str).astype(float)
    data["kB_rd/s"] = data['kB_rd/s'].astype(str).astype(float)
    data["kB_wr/s"] = data['kB_wr/s'].astype(str).astype(float)

    # convert memory usage to GB
    data["%MEM"] = data["%MEM"] * 1.87

    cpu = data["%CPU"][:-1]
    mem = data["%MEM"][:-1]
    # convert IO speed from KB/sec to GB/sec
    io = data["kB_rd/s"] / 1000000 + data["kB_wr/s"] / 1000000
    io = io[:-1]

    print("\n--------------------------------------------")
    print("Exact sort time: {} seconds".format(len(cpu)))
    IO = np.array(io)
    print("I/O utilization: {:.2f} MB/sec".format((np.sum(IO) / len(io))*1000))
    CPU = np.array(cpu)
    print("CPU utilization: {:.2f}%".format(np.sum(CPU)/len(cpu)))
    MEM = np.array(mem)
    print("Memory utilization: {:.2f} GB".format(np.sum(MEM) / len(mem)))
    print("---------------------------------------------\n")

    plot(cpu, mem, io)


