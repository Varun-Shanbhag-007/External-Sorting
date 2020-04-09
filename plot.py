import pandas as pd
import matplotlib.pyplot as plt
from mpl_toolkits.mplot3d import Axes3D
import numpy as np
import seaborn as sns



def read_columns(filename):
    with open(filename) as file:
        for line in file:
            if line[0] == '#':
                return line.split()[1:]


def get_line(filename, column):
    with open(filename) as file:
        for line in file:
         #   print(line[0])
            if line[0] == '1':
          #      print(line.split()[1:])
                yield line.split(maxsplit=column-1)


if __name__ == "__main__":

        filename = "64GB_data"
        columns = read_columns(filename)
        columns = columns[:] 
    
    

        df = pd.DataFrame.from_records(get_line(filename, len(columns)), columns=columns)

        df = df.drop(columns=["Command", "iodelay", "Time", "UID", "%usr", "%guest", "%system",
                              "majflt/s", "%wait", "CPU", "minflt/s", "RSS", "VSZ", "kB_ccwr/s"])
        #print(df)


        df['%CPU'] = df['%CPU'].astype(str).astype(float)
        df['%MEM'] = df['%MEM'].astype(str).astype(float)
        df["kB_rd/s"] = df['kB_rd/s'].astype(str).astype(float)
        df["kB_wr/s"] = df['kB_wr/s'].astype(str).astype(float)

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

