
using System;
using System.Collections.Generic;
using System.Data;
using Model;
using log4net;
using log4net.Util;

namespace Persistence
{
    public class TeamDBRepository : ITeamRepository
    {
        private static readonly ILog logger = LogManager.GetLogger("TeamDBRepository");

        IDictionary<String, string> props;

        public TeamDBRepository(IDictionary<String, string> props)
        {
            logger.Info("Creating TeamDBRepository");
            this.props = props;
        }

        public IEnumerable<Team> findAll()
        {
            logger.Info("Finding all teams");
            IList<Team> teams = new List<Team>();
            IDbConnection connection = DBUtils.getConnection(props);
            using (var command = connection.CreateCommand())
            {
                command.CommandText = "select id, name from teams";
                using (var result = command.ExecuteReader())
                {
                    while (result.Read())
                    {
                        int id = result.GetInt32(0);
                        string name = result.GetString(1);
                        Team team = new Team(id, name);
                        teams.Add(team);
                        
                        teams.Add(team);
                    }
                }
            }

            logger.InfoExt(teams);
            return teams;
        }

        public Team findOne(int id)
        {
            logger.Info("Finding team with id " + id);
            IDbConnection connection = DBUtils.getConnection(props);
            using (var command = connection.CreateCommand())
            {
                command.CommandText = "select id,name from teams where id=@id";
                IDbDataParameter paramId = command.CreateParameter();
                paramId.ParameterName = "@id";
                paramId.Value = id;
                command.Parameters.Add(paramId);
                using (var result = command.ExecuteReader())
                {
                    if (result.Read())
                    {
                        int idv = result.GetInt32(0);
                        string name = result.GetString(1);
                        Team team = new Team(idv, name);
                        logger.InfoExt(team);
                        return team;
                    }
                }
            }

            return null;
        }

        public Team save(Team entity)
        {
            throw new System.NotImplementedException();
        }

        public Team delete(int id)
        {
            throw new System.NotImplementedException();
        }

        public Team update(Team entity)
        {
            throw new System.NotImplementedException();
        }
    }
}