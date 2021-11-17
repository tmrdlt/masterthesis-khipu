var json = require('./data/temporal-query-results.json');
console.log(json)
const res = json.map(participant => {
    return {
        "participantId": participant.participantId,
        "task2_1_manual": [],
        "task2_1_proposal": participant.results[0].tasksResult.map(x => x.id),
        "task2_2_manual": [],
        "task2_2_proposal": participant.results[1].tasksResult.map(x => x.id)
    }
})
console.log(res)