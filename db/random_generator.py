import random


def generate():
    ids = set()
    with open("participants.csv") as fin, open("participants_with_id.csv", "w") as fout1, open("insert_participants.sql", "w") as fout2:
        lines = fin.readlines()
        for line in lines:
            name, email = line.strip().split(',')

            identifier = random.randint(100, 999)
            while identifier in ids:
                identifier = random.randint(100, 999)
            
            ids.add(identifier)

            fout1.write("%s,%s,%d\n" % (name, email, identifier))

            sql1 = "insert into gcm_users(role, status, phone, adviser, login_at) values('Dependant', 'paired', '%da', '%db', now());" % (identifier, identifier)
            sql2 = "insert into gcm_users(role, phone, login_at) values('Adviser', '%db', now());" % identifier
            sql3 = "insert into approvalstatus(adviser, dependantName, dependantPhone, status, time) values('%db', 'AdviseeForUserStudy', '%da', 'Accepted', now());" % (identifier, identifier)

            fout2.write(sql1 + "\n")
            fout2.write(sql2 + "\n")
            fout2.write(sql3 + "\n\n")

def not_submit():
    submitted = set()
    with open("submitted.csv") as fin:
        for line in fin.readlines():
            id, time, email = line.strip().split(',')
            submitted.add(id[:-1])
    
    with open("participants_with_id.csv") as fin:
        for line in fin.readlines():
            name, email, id = line.strip().split(',')
            if id not in submitted:
                print "%s,%s,%s" % (name, email, id)


def main():
    not_submit()

if __name__ == '__main__':
    main()