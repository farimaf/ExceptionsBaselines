import sys

our_data_loc=sys.argv[1]
exassist_data_loc=sys.argv[2]

out_loc=exassist_data_loc[0:exassist_data_loc.index(".txt")]+"_filteredByOurData.txt"
f_out = open(out_loc,"w",encoding="utf-8")

with open(our_data_loc,encoding="utf-8") as file_filtered:
    our_dataset = set()
    for line in file_filtered:
        linesplit=line.split("@#@")
        address=linesplit[0]
        method=linesplit[1].split("#")
        body, exceptions, indexes = method[len(method) - 3], method[len(method) - 2], method[len(method) - 1]
        lineToConsider=address+"@#@"+exceptions
        our_dataset.add(lineToConsider)

with open(our_data_loc,encoding="utf-8") as file_exAss:
    for line in file_exAss:
        linesplit=line.split("@#@")
        address=linesplit[0]
        method=linesplit[1].split("#")
        body, exceptions, indexes = method[len(method) - 3], method[len(method) - 2], method[len(method) - 1]
        lineToLook=address+"@#@"+exceptions
        if(lineToLook in our_dataset):
            f_out.write(line)
f_out.close()

