## Endpoints

### Check if server is healthy

```
GET /health
```

### Get all users

```
GET /user
```

### Create a new user

```
POST /user
```

#### Parameters

| Name       | Type   | In   | Description  |
|------------|--------|------|--------------|
| `username` | string | body | __Required__ |

### Get a single user

```
GET /user/{userApiId}
```

#### Parameters

| Name        | Type   | In   | Description  |
|-------------|--------|------|--------------|
| `userApiId` | string | path | __Required__ |

### Get all workflowlists

```
GET /workflowlist
```

#### Parameters

| Name        | Type   | In    | Description                         |
|-------------|--------|-------|-------------------------------------|
| `userApiId` | string | query | Only get of lists of specified user |

### Create a new workflowlist

```
POST /workflowlist
```

#### Parameters

| Name                        | Type                            | In   | Description  |
|-----------------------------|---------------------------------|------|--------------|
| `title`                     | string                          | body | __Required__ |
| `description`               | string                          | body |              |
| `listType`                  | WorkflowListType                | body | __Required__ |
| `parentApiId`               | boolean                         | body |              |
| `isTemporalConstraintBoard` | string                          | body |              |
| `children`                  | array(CreateWorkflowListEntity) | body | __Required__ |

### Update a workflowlist

```
PATCH /workflowlist/{workflowlistId}
```

#### Parameters

| Name                        | Type    | In   | Description  |
|-----------------------------|---------|------|--------------|
| `workflowlistId`            | string  | path | __Required__ |
| `newTitle`                  | string  | body | __Required__ |
| `newDescription`            | string  | body |              |
| `isTemporalConstraintBoard` | boolean | body |              |

### Delete a workflowlist

```
DELETE /workflowlist/{workflowlistId}
```

#### Parameters

| Name             | Type   | In   | Description  |
|------------------|--------|------|--------------|
| `workflowlistId` | string | path | __Required__ |

### Update a workflowlist's resources

```
POST /workflowlist/{workflowlistId}/resource
```

#### Parameters

| Name             | Type                         | In   | Description  |
|------------------|------------------------------|------|--------------|
| `workflowlistId` | string                       | path | __Required__ |
| `numeric`        | array(NumericResourceEntity) | body |              |
| `textual`        | array(TextualResourceEntity) | body |              |
| `temporal`       | TemporalResourceEntity       | body |              |
| `user`           | UserResourceEntity           | body |              |

### Convert a workflowlist to a new type

```
POST /workflowlist/{workflowlistId}/convert
```

#### Parameters

| Name             | Type             | In   | Description  |
|------------------|------------------|------|--------------|
| `workflowlistId` | string           | path | __Required__ |
| `newListType`    | WorkflowListType | body | __Required__ |

### Move a workflowlist to a new parent

```
POST /workflowlist/{workflowlistId}/move
```

#### Parameters

| Name             | Type    | In   | Description  |
|------------------|---------|------|--------------|
| `workflowlistId` | string  | path | __Required__ |
| `newParentApiId` | string  | body |              |
| `newPosition`    | integer | body |              |

### Reorder a workflowlist within its parent

```
POST /workflowlist/{workflowlistId}/reorder
```

#### Parameters

| Name             | Type    | In   | Description  |
|------------------|---------|------|--------------|
| `workflowlistId` | string  | path | __Required__ |
| `newPosition`    | integer | body | __Required__ |

### Perform a temporal query on a workflowlist

```
GET /workflowlist/{workflowlistId}/query
```

