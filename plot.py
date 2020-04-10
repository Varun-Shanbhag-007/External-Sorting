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
<<<<<<< HEAD
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
=======
         #   print(line[0])
            if line[0] == '1':
          #      print(line.split()[1:])
                yield line.split(maxsplit=column-1)
>>>>>>> 87789888a037a76a3ab8083653c222a44289c0a0


if __name__ == "__main__":

<<<<<<< HEAD
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
=======
        filename = "64GB_data"
        columns = read_columns(filename)
        columns = columns[:] 
    
    
>>>>>>> 87789888a037a76a3ab8083653c222a44289c0a0

    data.drop(data[data['PID'] != str(args.process_id)].index, inplace=True)

<<<<<<< HEAD
    # print(data)
=======
        df = df.drop(columns=["Command", "iodelay", "Time", "UID", "%usr", "%guest", "%system",
                              "majflt/s", "%wait", "CPU", "minflt/s", "RSS", "VSZ", "kB_ccwr/s"])
        #print(df)
>>>>>>> 87789888a037a76a3ab8083653c222a44289c0a0

    data['%CPU'] = data['%CPU'].astype(str).astype(float)
    data['%MEM'] = data['%MEM'].astype(str).astype(float)
    data["kB_rd/s"] = data['kB_rd/s'].astype(str).astype(float)
    data["kB_wr/s"] = data['kB_wr/s'].astype(str).astype(float)

    # convert memory usage to GB
    data["%MEM"] = data["%MEM"] * 1.87

<<<<<<< HEAD
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


=======
        df["%MEM"] = df["%MEM"]*1.87
        #print(df["%CPU"])

        # data1 = np.exp(t)
        data1 = df["%CPU"][:-1]
        # data2 = np.sin(2 * np.pi * t)
        data2 = df["%MEM"][:-1]
        #print(data2)
        # data3 = np.cos(2 * np.pi * t)
        data3 = df["kB_rd/s"]/1000000 + df["kB_wr/s"]/1000000
        data3 = data3[:-1]
        #print(data3)

        t = range(0,len(data3))
        fig, ax1 = plt.subplots()

        color = 'tab:red'
        ax1.set_xlabel('time (s)')
        ax1.set_ylabel('CPU (%)', color=color)
        ax1.plot(t, data1, color=color, label="CPU")
        ax1.tick_params(axis='y', labelcolor=color)
        plt.xticks(np.arange(0, 950, 50))
        ax1.legend(bbox_to_anchor=(0.062,1.006))

        ax2 = ax1.twinx()  # instantiate a second axes that shares the same x-axis

        color = 'tab:blue'
        ax2.set_ylabel('Memory (GB)', color=color)  # we already handled the x-label with ax1
        ax2.plot(t, data2, color=color, label="MEM")
        ax2.plot(t, data3, color='tab:orange', label="IO")
        ax2.tick_params(axis='y', labelcolor=color)
        
        plt.title("64GB - CPU, MEMORY, I/O")
        plt.yticks(np.arange(0, 10.5, 0.5))
        plt.legend()


        fig.tight_layout()  # otherwise the right y-label is slightly clipped
        plt.show()



        data3 = df["kB_rd/s"] / 1000 + df["kB_wr/s"] / 1000
        data3 = data3[:-1]
        plt.title("I/O utilization MB/sec")
        plt.xlabel("Time (seconds)")
        plt.ylabel("IO (MB/sec)")
        plt.xticks(np.arange(0,len(data3),50))
        plt.plot(t, data3, label="I/O",)
        plt.legend()
        plt.show()

>>>>>>> 87789888a037a76a3ab8083653c222a44289c0a0
