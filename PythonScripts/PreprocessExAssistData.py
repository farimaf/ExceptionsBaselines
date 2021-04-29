#eexassist train and test set does not need preprocessing cause they are already created based on our train and test set which are already preprocessed
filter_train=False # if we wanna filter rows that are in train too
train_path="/scratch/mondego/local/farima/drex/baselines/ExceptionsBaselines/src/Train_PerTryNoRunTimeLiteral_Consolidated_ExAssist_WithLineInfo.txt"
lenLine=4 #for exass data this is 3, for our tool data this is 4

exception_dir="/scratch/mondego/local/farima/drex/baselines/ExceptionsBaselines/ExcpetionsToPredictWithFreq.txt"
data_dir = '/scratch/mondego/local/farima/datasets/FamousProjects/Jan30-2021/processed-files/OurTool/forYadong/PerTryNoRuntimeLiteral_repos-zips_xerces2-j-trunk.txt'

with open(exception_dir) as file_in:
    exceptions = set()
    for line in file_in:
        linesplit=line.split(":")
        exceptions.add(linesplit[0])

trainset = set()
if(filter_train):
    with open(train_path,encoding="utf-8") as file_tr:
        trainset = set()
        for line in file_tr:
            bodyparts = line.strip('\n').split("@#@")[1]
            trainset.add(bodyparts)

f_out = open("/scratch/mondego/local/farima/datasets/FamousProjects/Jan30-2021/processed-files/OurTool/forYadong/PerTryNoRuntimeLiteral_repos-zips_xerces2-j-trunk-PreProcessed.txt",
             "w",encoding="utf-8")
with open(data_dir, 'r',encoding="utf-8") as f:
    null_cnt = 0
    mim_cnt = 0
    multi_except_cnt = 0
    no_try_cnt = 0
    lines_in_train=0
    keep_idx = []
    prev_line = None
    num_other_excep=0
    name, body, loc, exception = [], [], [], []
    for i, line in enumerate(f):
        if line == prev_line:
            print('repeated line')
            continue
        method = line.strip('\n').split('@#@')[1].split('#')
        if len(method) > lenLine:
            print(line)
            mim_cnt += 1
            continue
        # t1: body; t2: exception; t3: location
        t1, t2, t3 = method[len(method)-3].split(','), method[len(method)-2].split(','), method[len(method)-1].split(',')
        if (filter_train):
            lineTocheck = line.strip('\n').split('@#@')[1]
            if(lineTocheck in trainset):
                lines_in_train+=1
                continue

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
print("num in train: "+str(lines_in_train))
print("num more than one excep: "+str(multi_except_cnt))
print("num neg index: "+str(no_try_cnt))
print("num method in method "+str(mim_cnt))
print("num other exceptions "+str(num_other_excep))
