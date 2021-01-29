exception_dir="C:\\Users\\Farima\\OneDrive\\Data\\PhD\\Exception Type Recommendation\\" \
              "ExAssistReplication\\EvalBaseLines\\ExcpetionsToPredictWithFreq.txt"
data_dir = 'C:\\Users\\Farima\\OneDrive\\Data\\PhD\\Exception Type Recommendation\\TopJavaProjects-ForEval-FSE21' \
           '\\ProcessedForExAssBaseline\\PerTryNoRuntimeLiteral_AllTopProj_Post2018.txt'

with open(exception_dir) as file_in:
    exceptions = set()
    for line in file_in:
        linesplit=line.split(":")
        exceptions.add(linesplit[0])

f_out = open("C:\\Users\\Farima\\OneDrive\\Data\\PhD\\Exception Type Recommendation\\TopJavaProjects-ForEval-FSE21\\ProcessedForExAssBaseline\\PerTryNoRuntimeLiteral_AllTopProj_Post2018_PreProcessed.txt",
             "w",encoding="utf-8")

with open(data_dir, 'r',encoding="utf-8") as f:
    null_cnt = 0
    mim_cnt = 0
    multi_except_cnt = 0
    no_try_cnt = 0
    keep_idx = []
    prev_line = None
    num_other_excep=0
    name, body, loc, exception = [], [], [], []
    for i, line in enumerate(f):
        if line == prev_line:
            print('repeated line')
            continue
        method = line.strip('\n').split('@#@')[1].split('#')
        if len(method) > 3:
            mim_cnt += 1
            continue
        # t1: body; t2: exception; t3: location
        t1, t2, t3 = method[0].split(','), method[1].split(','), method[2].split(',')
        if len(t2) > 1:
            multi_except_cnt += 1
        excep=t2[0]
        if(excep not in exceptions):
            num_other_excep+=1
            continue
        if t3 == ['-1', '-1']:
            no_try_cnt += 1
            continue
        f_out.write(line)
f_out.close()
print("num more than one excep: "+str(multi_except_cnt))
print("num neg index: "+str(no_try_cnt))
print("num method in method "+str(mim_cnt))
print("num other exceptions "+str(num_other_excep))